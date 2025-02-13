package ul.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriterFactory;
import javax.json.JsonException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javax.json.JsonWriter;

public class Server {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        boolean serverRunning = true;

        try (ServerSocket servSock = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT);

            while (serverRunning) {
                Socket link = null;
                try {
                    link = servSock.accept();
                    System.out.println("Client connected: " + link.getInetAddress().getHostAddress());

                    try (BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
                         PrintWriter out = new PrintWriter(link.getOutputStream(), true)) {

                        String request;
                        while ((request = requestReader(in)) != null) {
                            System.out.println("Received: " + request);

                            if (request.equals("STOP")) {
                                System.out.println("Client requested termination. Shutting down server.");
                                out.println("TERMINATE");
                                out.flush();
                                link.close();
                                serverRunning = false;
                                break;
                            }

                            String response = responseBuilder(request);
                            System.out.println("Sending: " + response);
                            out.println(response);
                            out.flush();
                        }
                        System.out.println("Client disconnected: " + link.getInetAddress().getHostAddress());

                    } catch (IOException e) {
                        System.err.println("IO Error in client handling: " + e.getMessage());
                    }
                } catch (IOException e) {
                    System.err.println("IO Error during accept: " + e.getMessage());
                } finally {
                    try {
                        if (link != null && !link.isClosed()) {
                            link.close();
                        }
                    } catch (SocketException e) {
                        System.err.println("Socket Error: " + e.getMessage());
                    }
                }
            }
            System.out.println("Server shutting down.");

        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
        }
    }

    private static String requestReader(BufferedReader in) throws IOException {
        return in.readLine();
    }

    private static String responseBuilder(String request) {
        try {
            // Deserialize req
            StringReader stringReader = new StringReader(request);
            JsonReader jsonReader = Json.createReader(stringReader);
            JsonObject requestData = jsonReader.readObject();

            // Extract headers and data
            JsonObject headers = requestData.getJsonObject("headers");
            JsonObject data = requestData.getJsonObject("data");

            for (String key : headers.keySet()) {
                switch (key) {
                    case "error":
                        throw new Exception("Error header present");
                    case "test":
                        System.out.println("Test header present");
                        break;
                    default:
                        break;
                }
            }

            String message = data.toString();

            // Build response
            JsonObject responseData =
                    Json.createObjectBuilder().add("status", "success").add("message", message).build();

            return jsonToString(responseData);

        } catch (JsonException e) {
            System.err.println("JsonException occurred: " + e.getMessage());
            return errorBuilder(e);
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            return errorBuilder(e);
        }
    }

    private static String jsonToString(JsonObject jsonObject) {
        // Serialization config
        Map<String, Object> responseConfig = new HashMap<>();
        // Enable pretty printing (Currently broken)
        // responseConfig.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory writerFactory = Json.createWriterFactory(responseConfig);

        StringWriter stringWriter = new StringWriter();
        try (JsonWriter jsonWriter = writerFactory.createWriter(stringWriter)) {
            jsonWriter.writeObject(jsonObject);
        }
        return stringWriter.toString();
    }


    private static String errorBuilder(Exception e) {
        JsonObject errorResponse =
                Json.createObjectBuilder()
                        .add("status", "error")
                        .add("message", "Server error: " + e.getMessage())
                        .build();
        return jsonToString(errorResponse);
    }
}