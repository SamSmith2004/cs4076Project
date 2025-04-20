package ul.cs4076project.Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import jakarta.json.*;
import javafx.application.Platform;

public class TCPClient {
    private static final int PORT = 8080;
    private Socket link;
    private BufferedReader in;
    private PrintWriter out;
    private boolean isConnected = false;
    private boolean isListening = false;
    // Executor Thread Pool for handling promises
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    // Event Listener
    private Consumer<ResponseType> updateListener;

    public TCPClient() throws IOException {connect();}

    private void connect() throws IOException {
        try {
            link = new Socket("localhost", PORT);
            in = new BufferedReader(new InputStreamReader(link.getInputStream()));
            out = new PrintWriter(link.getOutputStream(), true);
            isConnected = true;
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + e.getMessage());
            isConnected = false;
            throw e;
        } catch (ConnectException e) {
            System.err.println("Connection refused: " + e.getMessage());
            isConnected = false;
            throw e;
        } catch (IOException e) {
            System.err.println("Error creating socket: " + e.getMessage());
            isConnected = false;
            throw e;
        }
    }

    public boolean isConnected() {
        return isConnected && link != null && link.isConnected() && !link.isClosed();
    }

    // All requests types act as promises
    public CompletableFuture<ResponseType> get(String message, Map<String, String> headers) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return sendRequest("GET", message, headers);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }

    public CompletableFuture<ResponseType> post(String message, Map<String, String> headers) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return sendRequest("POST", message, headers);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }

    public CompletableFuture<ResponseType> update(String message, Map<String, String> headers) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return sendRequest("UPDATE", message, headers);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }
    public CompletableFuture<ResponseType> create(String message, Map<String, String> headers) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return sendRequest("CREATE", message, headers);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }

    public void setUpdateListener(Consumer<ResponseType> listener) {
        this.updateListener = listener;
        if (!isListening) {
            startListening();
        }
    }

    private void startListening() {
        isListening = true;
        executorService.submit(new ServerListener());
    }

    private synchronized ResponseType sendRequest(String methodType, String message, Map<String, String> headers)
            throws IOException {
        if (!isConnected()) {
            try {
                connect();
            } catch (IOException e) {
                throw new IOException("Failed to reconnect", e);
            }
        }

        try {
            // Headers
            JsonObjectBuilder headersBuilder = Json.createObjectBuilder();
            headers.forEach(headersBuilder::add);

            // Build + Send JSON req
            JsonObjectBuilder contentBuilder = Json.createObjectBuilder();
            if (message.startsWith("{")) {
                JsonReader jsonReader = Json.createReader(new StringReader(message));
                contentBuilder = Json.createObjectBuilder(jsonReader.readObject());
            } else {
                contentBuilder.add("message", message);
            }

            JsonObject jsonRequest = Json.createObjectBuilder()
                    .add("method", methodType)
                    .add("headers", headersBuilder)
                    .add("content", contentBuilder)
                    .build();
            out.println(jsonRequest.toString());

            // Read response
            String response = in.readLine();
            if (response == null) {
                System.err.println("Response is null.");
                throw new IOException("Response is null");
            }
            System.out.println("Received: " + response);

            JsonReader jsonReader = Json.createReader(new StringReader(response));
            return new ResponseHandler(jsonReader.readObject()).extractResponse();
        } catch (JsonException e) {
            System.err.println("JSON error during " + methodType + ": " + e.getMessage());
            throw e;
        } catch (SocketException e) {
            System.err.println("Socket error during " + methodType + ": " + e.getMessage());
            isConnected = false;
            throw e;
        } catch (IOException e) {
            System.err.println("IO Error during " + methodType + ": " + e.getMessage());
            isConnected = false;
            throw e;
        }
    }

    private class ServerListener implements Runnable {
        @Override
        public void run() {
            try {
                while (isListening && isConnected()) {
                    try {
                        if (in.ready()) {
                            String response = in.readLine();
                            if (response != null) {
                                System.out.println("Received update: " + response);
                                if (updateListener != null) {
                                    JsonReader jsonReader = Json.createReader(new StringReader(response));
                                    final ResponseType responseType = new ResponseHandler(jsonReader.readObject()).extractResponse();

                                    Platform.runLater(() -> updateListener.accept(responseType));
                                }
                            }
                        }
                        // This prevents a bug do not remove
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("Listener thread interrupted: " + e.getMessage());
                        isListening = false;
                    } catch (Exception e) {
                        System.err.println("Error in listener thread: " + e.getMessage());
                        if (!isConnected()) {
                            isListening = false;
                        }
                    }
                }
            } finally {
                System.out.println("Server listener stopped");
            }
        }
    }

    public void close() {
        isListening = false;
        try {
            if (isConnected()) {
                out.println("STOP");
                String response = in.readLine();
                System.out.println("Received: " + response);

                if (response.equals("TERMINATE")) {
                    in.close();
                    out.close();
                    link.close();
                    isConnected = false;
                } else {
                    System.err.println("Error closing connection: " + response);
                }
            }
        } catch (IOException e) {
            System.err.println("IO Error during close: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }
    }
}