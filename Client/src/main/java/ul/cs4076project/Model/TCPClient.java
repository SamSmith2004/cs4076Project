package ul.cs4076project.Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class TCPClient {
    private static final int PORT = 8080;
    private Socket link;
    private BufferedReader in;
    private PrintWriter out;

    public TCPClient() throws IOException {
        try {
            link = new Socket("localhost", PORT);
            in = new BufferedReader(new InputStreamReader(link.getInputStream()));
            out = new PrintWriter(link.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Error creating socket: " + e.getMessage());
        }
    }

    public JsonObject post(String message) throws IOException {
        try {
            // Create JSON request
            JsonObject jsonRequest =
                    Json.createObjectBuilder()
                            .add("headers", Json.createObjectBuilder().build())
                            .add("data", Json.createObjectBuilder().add("item", message).build())
                            .build();

            // Send JSON request
            out.println(jsonRequest.toString());

            // Read JSON response
            String response = in.readLine();
            System.out.println("Received: " + response);
            JsonReader jsonReader = Json.createReader(new StringReader(response));

            return jsonReader.readObject();
        } catch (IOException e) {
            System.err.println("IO Error during POST: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error during POST: " + e.getMessage());
            throw e;
        }
    }

    public void close() {
        try {
            out.println("STOP");

            // Read JSON response
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
            System.err.println("IO Error during POST: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error during POST: " + e.getMessage());
            throw e;
        }
    }

    public static void main(String[] args) {
        try {
            TCPClient client = new TCPClient();
            BufferedReader userEntry = new BufferedReader(new InputStreamReader(System.in));
            String message;

            System.out.println("Enter message to be sent to server (or 'exit' to quit):");
            while ((message = userEntry.readLine()) != null) {
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }

                JsonObject response = client.post(message);
                System.out.println("\nSERVER RESPONSE> " + response.toString());

                System.out.println("Enter message to be sent to server (or 'exit' to quit):");
            }

            client.close();
        } catch (IOException e) {
            System.err.println("IO Exception: " + e.getMessage());
        }
    }
}
