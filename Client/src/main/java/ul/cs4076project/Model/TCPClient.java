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
import jakarta.json.*;

/**
 * A TCP client implementation that handles communication with a server using
 * JSON messages. This class manages socket connections and provides methods for
 * sending GET, POST and CREATE requests. All communication is done using JSON
 * formatted messages.
 */
public class TCPClient {
    /**
     * The port number used to connect to the server.
     */
    private static final int PORT = 8080;

    /**
     * The socket connection to the server.
     */
    private final Socket link;

    /**
     * Buffered reader for reading responses from the server.
     */
    private final BufferedReader in;

    /**
     * PrintWriter for sending requests to the server.
     */
    private final PrintWriter out;

    /**
     * Flag indicating whether the client is currently connected to the server.
     */
    private boolean isConnected = false;

    /**
     * Constructs a new TCPClient and establishes a connection to the server. The
     * client connects to localhost on the specified PORT.
     *
     * @throws IOException          if there is an error creating the socket
     *                              connection
     * @throws UnknownHostException if the host cannot be found
     * @throws ConnectException     if the connection is refused
     */
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

    /**
     * Checks if the client is currently connected to the server.
     *
     * @return true if the client is connected and the socket is open, false
     *         otherwise
     */
    public boolean isConnected() {
        return isConnected && link != null && link.isConnected() && !link.isClosed();
    }

    /**
     * Sends a GET request to the server.
     *
     * @param message the message content to send
     * @param headers a map of headers to include in the request
     * @return a {@link ResponseType} object containing the server's response
     * @throws IOException if there is an error during communication
     */
    public ResponseType get(String message, Map<String, String> headers) throws IOException {
        return sendRequest("GET", message, headers);
    }

    /**
     * Sends a POST request to the server.
     *
     * @param message the message content to send
     * @param headers a map of headers to include in the request
     * @return a {@link ResponseType} object containing the server's response
     * @throws IOException if there is an error during communication
     */
    public ResponseType post(String message, Map<String, String> headers) throws IOException {
        return sendRequest("POST", message, headers);
    }

    /**
     * Sends a CREATE request to the server.
     *
     * @param message the message content to send
     * @param headers a map of headers to include in the request
     * @return a {@link ResponseType} object containing the server's response
     * @throws IOException if there is an error during communication
     */
    public ResponseType create(String message, Map<String, String> headers) throws IOException {
        return sendRequest("CREATE", message, headers);
    }

    /**
     * Sends a request to the server with the specified method type. The request is
     * formatted as a JSON object containing the method type, headers, and content.
     *
     * @param methodType the method type (GET, POST, CREATE)
     * @param message    the message content to send
     * @param headers    a map of headers to include in the request
     * @return a {@link ResponseType} object containing the server's response
     * @see ul.cs4076project.Model.ResponseHandler
     * @throws IOException     if there is an error during communication
     * @throws JsonException   if there is an error processing JSON
     * @throws SocketException if there is an error with the socket connection
     */
    private ResponseType sendRequest(String methodType, String message, Map<String, String> headers)
            throws IOException {
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

    /**
     * Closes the connection to the server. Sends a STOP message and waits for a
     * TERMINATE response before closing all streams and the socket. Any errors
     * during closing are logged but not thrown.
     */
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