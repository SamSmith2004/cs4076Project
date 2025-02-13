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
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.JsonException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

// Testing:
// echo '{"headers": {}, "data": {"item": "1"}}' | nc localhost 8080
// echo "STOP" | nc localhost 8080

public class App {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        // Basic TCP server using Try with Resources
        try (ServerSocket servSock = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT);
            boolean run = true;
            while (run) {
                Socket link = null;
                try {
                    link = servSock.accept();

                    BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
                    PrintWriter out = new PrintWriter(link.getOutputStream(), true);

                    String request = requestReader(in);
                    System.out.println("Received: " + request);

                    String response = responseBuilder(request);
                    System.out.println("Sending: " + response);
                    out.println(response);
                    out.flush();

                    if (response.equals("TERMINATE")) {
                        System.out.println("Server shutting down");
                        run = false;
                    }

                } catch (IOException e) {
                    System.err.println("IO Error: " + e.getMessage());
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
        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static String requestReader(BufferedReader in) throws IOException {
        return in.readLine();
    }

    private static String responseBuilder(String request) {
        if (request.equals("STOP")) {
            return "TERMINATE";
        }
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
