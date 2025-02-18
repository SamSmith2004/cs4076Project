package ul.Server;

import ul.Server.Handlers.Get;
import ul.Server.Handlers.Post;
import ul.Server.Utils.IncorrectActionException;
import ul.Server.Utils.SessionData;

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
import java.io.StringReader;

import static java.lang.System.out;

public class Server {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        boolean serverRunning = true;
        SessionData sessionData = new SessionData();

        try (ServerSocket servSock = new ServerSocket(PORT)) {
            out.println("Server listening on port " + PORT);
            sessionData.fillMockData();

            while (serverRunning) {
                Socket link = null;
                try {
                    link = servSock.accept();
                    out.println("Client connected: " + link.getInetAddress().getHostAddress());

                    try (BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
                         PrintWriter out = new PrintWriter(link.getOutputStream(), true)) {

                        String request;
                        while ((request = in.readLine()) != null) {
                            System.out.println("Received: " + request);

                            if (request.equals("STOP")) {
                                System.out.println("Client requested termination. Shutting down server.");
                                out.println("TERMINATE");
                                out.flush();
                                link.close();
                                serverRunning = false;
                                break;
                            }

                            try {
                                StringReader stringReader = new StringReader(request);
                                JsonReader jsonReader = Json.createReader(stringReader);
                                JsonObject requestData = jsonReader.readObject();

                                String response;
                                switch (requestData.getString("method")) {
                                    case "GET" -> {
                                        Get get = new Get(requestData);
                                        response = get.responseBuilder(sessionData);
                                    }
                                    case "POST" -> {
                                        Post post = new Post(requestData);
                                        response =  post.responseBuilder(sessionData);
                                    }
                                    default -> throw new IncorrectActionException();
                                };

                                System.out.println("Sending: " + response);
                                out.println(response);
                                out.flush();
                            } catch (IncorrectActionException e) {
                                JsonObject response = Json.createObjectBuilder()
                                        .add("status", "InvalidActionException")
                                        .add("content", "Invalid method")
                                        .add("Content-Type", "Exception")
                                        .build();

                                System.out.println("Sending: " + response);
                                out.println(response);
                                out.flush();
                            } catch (IOException e) {
                                JsonObject response = Json.createObjectBuilder()
                                        .add("status", "error")
                                        .add("content", e.getMessage())
                                        .add("Content-Type", "Exception")
                                        .build();

                                System.out.println("Sending: " + response);
                                out.println(response);
                                out.flush();
                            }
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
            out.println("Server shutting down.");

        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
        }
    }
}