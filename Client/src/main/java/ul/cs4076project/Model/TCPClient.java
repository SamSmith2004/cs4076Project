package ul.cs4076project.Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

public class TCPClient {
    private static final int PORT = 8080;
    private final Socket link;
    private final BufferedReader in;
    private final PrintWriter out;

    public TCPClient() throws IOException {
        try {
            link = new Socket("localhost", PORT);
            in = new BufferedReader(new InputStreamReader(link.getInputStream()));
            out = new PrintWriter(link.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Error creating socket: " + e.getMessage());
            throw e;
        }
    }

    public Object get(String message, Map<String, String> headers) throws IOException {
        return sendRequest("GET", message, headers);
    }

    public Object post(String message, Map<String, String> headers) throws IOException {
        return sendRequest("POST", message, headers);
    }

    private Object sendRequest(String methodType, String message, Map<String, String> headers) throws IOException {
        try {
            // Get headers
            JsonObjectBuilder headersBuilder = Json.createObjectBuilder();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headersBuilder.add(entry.getKey(), entry.getValue());
            }

            // Build JSON request
            JsonObject jsonRequest = Json.createObjectBuilder()
                    .add("method", methodType)
                    .add("headers", headersBuilder)
                    .add("content", Json.createObjectBuilder()
                            .add("message", message)
                            .build())
                    .build();

            out.println(jsonRequest.toString());

            // Read response
            String response = in.readLine();
            System.out.println("Received: " + response);
            JsonReader jsonReader = Json.createReader(new StringReader(response));

            ResponseHandler parsedResponse = new ResponseHandler(jsonReader.readObject());
            return parsedResponse.extractResponse();
        } catch (IOException e) {
            System.err.println("IO Error during " + methodType + ": " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error during " + methodType + ": " + e.getMessage());
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