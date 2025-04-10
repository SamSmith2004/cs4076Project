package ul.cs4076projectserver;

import static java.lang.System.out;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;

import jakarta.json.stream.JsonParsingException;
import ul.cs4076projectserver.Handlers.*;
import ul.cs4076projectserver.Models.IncorrectActionException;

public class Server {
    private static final int PORT = 8080;
    private static Connection dbConnection;
    private static DBManager dbManager;
    private static ArrayList<Object> clients = new ArrayList<>();
    protected static boolean serverRunning;

    public Server() {
        serverRunning = true;

        try {
            dbConnection = initializeDatabase();
            dbManager = new DBManager(dbConnection);
            System.out.println("Connected to database successfully");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return;
        }

        try (ServerSocket servSock = new ServerSocket(PORT)) {
            while (serverRunning) {
                Socket clientSocket = servSock.accept();
                clients.add(clientSocket);
                System.out.println("New client connected");

                // New thread for each client
                ClientHandler handler = new ClientHandler(clientSocket);
                Thread thread = new Thread(handler);
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("Unable to attach to port!");
            System.exit(1);
        }
    }

    private static Connection initializeDatabase() throws SQLException {
        // F*ck paths
        Dotenv dotenv = Dotenv.configure()
            .directory("Server/src/main/resources")
            .load();

        String url = String.format(
            "jdbc:postgresql://%s:%s/%s",
            dotenv.get("DB_HOST"),
            dotenv.get("DB_PORT"),
            dotenv.get("DB_NAME")
        );
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        return DriverManager.getConnection(url, user, password);
    }

    public static DBManager getDatabaseManager() {return dbManager;}

    static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            Socket link = clientSocket;
            try {
                out.println("Client connected: " + link.getInetAddress().getHostAddress());
                try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
                    PrintWriter out = new PrintWriter(link.getOutputStream(), true)
                ) {
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
                            JsonObject requestData =jsonReader.readObject();

                            String response;
                            switch (requestData.getString("method")) {
                                case "GET" -> {
                                    Get get = new Get(requestData);
                                    response = get.responseBuilder();
                                }
                                case "POST" -> {
                                    Post post = new Post(requestData);
                                    response = post.responseBuilder();
                                }
                                case "UPDATE" -> {
                                    Update update = new Update(requestData);
                                    response = update.responseBuilder();
                                }
                                default -> throw new IncorrectActionException();
                            }

                            System.out.println("Sending: " + response);
                            out.println(response);
                            out.flush();
                            out.flush();
                        } catch (JsonParsingException e) {
                            System.err.println("JSON parsing error: " + e.getMessage());
                            JsonObject errorResponse = Json.createObjectBuilder()
                                    .add("status", "error")
                                    .add("content", "Malformed JSON input")
                                    .add("Content-Type", "Exception")
                                    .build();
                            out.println(errorResponse);
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
                    System.out.println("Client Disconnected: " + link.getInetAddress().getHostAddress());
                } catch (IOException e) {
                    System.err.println("IO ERROR in Client Handling: " + e.getMessage());
                } finally {
                    try {
                        if (link != null && !link.isClosed()) {
                            link.close();
                        }
                    } catch (SocketException e) {
                        System.err.println("Socket Error: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.err.println("IO Error: " + e.getMessage());
            }
        }
    }
}