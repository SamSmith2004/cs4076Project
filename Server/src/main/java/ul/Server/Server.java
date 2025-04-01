package ul.Server;

import ul.Server.Handlers.Get;
import ul.Server.Handlers.Post;
import ul.Server.Handlers.DBManager;
import ul.Server.Handlers.Update;
import ul.Server.Models.IncorrectActionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Paths;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.StringReader;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static java.lang.System.out;

/**
 * The {@code Server} class is responsible for initiating and running the server
 * application.
 * It initializes the database connection, starts the server socket, and listens
 * for incoming client connections.
 * The server handles custom GET and POST requests and can be terminated by a
 * client sending a "STOP" request.
 */
public class Server {
    /**
     * The port number on which the server listens for. Can be changed to any other
     * port if 8080 is already being used on the host system.
     */
    private static final int PORT = 8080;
    /**
     * The connection object used to interact with the database.
     */
    private static Connection dbConnection;
    /**
     * The manager responsible for handling database operations.
     * 
     * @see ul.Server.Handlers.DBManager
     */
    private static DBManager dbManager;

    /**
     * Default arg-less {@link Server} constructor - not used
     */
    public Server() {

    }

    /**
     * Method used to initialise a connection with the PostgresSQL database by
     * loading the database credentials from the .env file provided in the current
     * directory. The method first gets the path of the environment variable and
     * parses it to retrieve the url and database host, port, name and user
     * credentials.
     * 
     * @return A {@link Connection} object that represents a connection to the
     *         database.
     * @throws SQLException If a database error occurs, or the URL is null.
     */
    private static Connection initializeDatabase() throws SQLException {
        // Painful method to get the path of the .env file
        String envPath = Paths.get("Server", "src", "main", "java", "ul", "Server").toString();
        Dotenv dotenv = Dotenv.configure().directory(envPath).load();

        // Get env data
        String url = String.format("jdbc:postgresql://%s:%s/%s",
                dotenv.get("DB_HOST"),
                dotenv.get("DB_PORT"),
                dotenv.get("DB_NAME"));
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Accessor method used to retrieve the database instance. This database manager
     * is responsible for handling all relevant database operations
     * 
     * @return The {@link DBManager} instance
     * @see ul.Server.Handlers.DBManager
     */
    public static DBManager getDatabaseManager() {
        return dbManager;
    }

    /**
     * The main method is used to initiate the server application. It first
     * initialises the database connection, then proceeds to start the server socket
     * which then allows the server to listen for any incoming client connections.
     * The server is able to handle custom {@link ul.Server.Handlers.Get} and
     * {@link ul.Server.Handlers.Post} requests. The server is able
     * to be terminated through the use of a 'STOP' request received from the
     * client. If the user in the client tries to achieve outside the scope of the
     * server's capability, a custom {@link IncorrectActionException} error is
     * thrown.
     * 
     * @param args (not used)
     * @see ul.Server.Handlers.Get
     * @see ul.Server.Handlers.Post
     * @see ul.Server.Models.IncorrectActionException
     */
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
}