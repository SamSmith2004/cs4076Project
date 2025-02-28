package ul.Server;

import ul.Server.Handlers.Get;
import ul.Server.Handlers.Post;
import ul.Server.Utils.DBManager;
import ul.Server.Utils.IncorrectActionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Paths;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static java.lang.System.out;

public class Server {
    private static final int PORT = 8080;
    private static Connection dbConnection;
    private static DBManager dbManager;

    private static Connection initializeDatabase() throws SQLException {
        // Painful method to get the path of the .env file
        String envPath = Paths.get("Server", "src", "main", "java", "ul", "Server").toString();
        Dotenv dotenv = Dotenv.configure().directory(envPath).load();

        // Get env data
        String url = String.format("jdbc:postgresql://%s:%s/%s",
                dotenv.get("DB_HOST"),
                dotenv.get("DB_PORT"),
                dotenv.get("DB_NAME")
        );
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        return DriverManager.getConnection(url, user, password);
    }

    public static DBManager getDatabaseManager() {
        return dbManager;
    }

    public static void main(String[] args) {
        boolean serverRunning = true;

        // Initialize database connection
        try {
            dbConnection = initializeDatabase();
            dbManager = new DBManager(dbConnection);
            System.out.println("Connected to database successfully");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return;
        }

        try (ServerSocket servSock = new ServerSocket(PORT)) {
            out.println("Server listening on port " + PORT);

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

                            // Debug
                            if (request.equals("DEBUG")) {
                                out.println(testDB());
                                out.flush();
                                continue;
                            }
                            //

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
                                        response = get.responseBuilder();
                                    }
                                    case "POST" -> {
                                        Post post = new Post(requestData);
                                        response =  post.responseBuilder();
                                    }
                                    default -> throw new IncorrectActionException();
                                }

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
                        System.out.println("Client Disconnected: " + link.getInetAddress().getHostAddress());

                    } catch (IOException e) {
                        System.err.println("IO ERROR in Client Handling: " + e.getMessage());
                    }
                } catch (IOException e) {
                    System.err.println("IO ERROR During Accept: " + e.getMessage());
                } finally {
                    try {
                        if (link != null && !link.isClosed()) {
                            link.close();
                        }
                        if (dbConnection != null) {
                            dbConnection.close();
                        }
                    } catch (SocketException e) {
                        System.err.println("Socket Error: " + e.getMessage());
                    } catch (SQLException e) {
                        System.err.println("Error closing database connection: " + e.getMessage());
                    }
                }
            }
            out.println("Server Shutting Down.");

        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
        }
    }

    private static String testDB() {
        try {
            var result = dbConnection.createStatement().executeQuery("SELECT * FROM lectures");
            int count = 0;
            StringBuilder response = new StringBuilder();
            while (result.next()) {
                count++;
                response.append("ID: ").append(result.getInt("id"))
                        .append(", Module: ").append(result.getString("module"))
                        .append(", Lecturer: ").append(result.getString("lecturer"))
                        .append(", Room: ").append(result.getString("room"))
                        .append(", Time: ").append(result.getString("from_time"))
                        .append("-").append(result.getString("to_time"))
                        .append(", Day: ").append(result.getString("day"))
                        .append("\n");
            }
            System.out.println("Query completed. Found " + count + " rows.");
            return response.toString();
        } catch (SQLException e) {
            System.err.println("Database query failed: " + e.getMessage());
            return "Database query failed: " + e.getMessage();
        }
    }
}