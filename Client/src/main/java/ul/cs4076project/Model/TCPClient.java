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
import javax.json.*;

public class TCPClient {
    private static final int PORT = 8080;
    private final Socket link;
    private final BufferedReader in;
    private final PrintWriter out;
    private boolean isConnected = false;

    public TCPClient() throws IOException {
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

    public ResponseType get(String message, Map<String, String> headers) throws IOException {
        return sendRequest("GET", message, headers);
    }

    public ResponseType post(String message, Map<String, String> headers) throws IOException {
        return sendRequest("POST", message, headers);
    }

    public ResponseType create(String message, Map<String, String> headers) throws IOException {
        return sendRequest("CREATE", message, headers);
    }

    private ResponseType sendRequest(String methodType, String message, Map<String, String> headers) throws IOException {
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

    public void close() {
        try {
            out.println("STOP");
            String response = in.readLine();
            System.out.println("Received: " + response);

            if (response.equals("TERMINATE")) {
                in.close();
                out.close();
                link.close();
            } else {
                System.err.println("Error closing connection: " + response);
            }
        } catch (IOException e) {
            System.err.println("IO Error during close: " + e.getMessage());
        }
    }
}