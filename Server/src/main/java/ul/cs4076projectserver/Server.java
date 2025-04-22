package ul.cs4076projectserver;

import static java.lang.System.out;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.concurrent.CompletableFuture;

import jakarta.json.stream.JsonParsingException;
import ul.cs4076projectserver.Handlers.*;
import ul.cs4076projectserver.Models.DB_instance;
import ul.cs4076projectserver.Models.IncorrectActionException;
import ul.cs4076projectserver.Models.Lecture;

public class Server {
    private static final int PORT = 8080;
    private static Connection dbConnection;
    private static DBManager dbManager;
    private static final ArrayList<PrintWriter> clientWriters = new ArrayList<>();
    private static ArrayList<Lecture> lectureList;
    protected static boolean serverRunning;
    private static ServerSocket servSock;

    public Server() {
        serverRunning = true;
        CompletableFuture.supplyAsync(() -> {
            try {
                dbConnection = initializeDatabase();
                dbManager = new DBManager(dbConnection);
                out.println("Connected to database successfully");
                return null;
            } catch (SQLException e) {
                System.err.println("Error initializing database: " + e.getMessage());
                return null;
            }
        }).thenAccept(result -> {
            try {
                fillLectureList();
            } catch (SQLException e) {
                System.err.println("Error filling lecture list: " + e.getMessage());
            }
        });
    }

    public void startServer() {
        try {
            servSock = new ServerSocket(PORT);
            while (serverRunning) {
                try {
                    Socket clientSocket = servSock.accept();
                    out.println("New client connected");
                    // New thread for each client
                    ClientHandler handler = new ClientHandler(clientSocket);
                    Thread thread = new Thread(handler);
                    thread.start();
                } catch (IOException ex) {
                    if (!serverRunning)
                        break;
                    System.err.println("Error accepting client connection: " + ex.getMessage());
                }
            }
        } catch (IOException e) {
            out.println("IOERROR: Unable to start server");
            System.exit(1);
        } finally {
            if (servSock != null && !servSock.isClosed()) {
                try {
                    servSock.close();
                } catch (IOException e) {
                    System.err.println("Error closing server socket: " + e.getMessage());
                }
            }
        }
    }

    public void stopServer() {
        out.println("Stopping server");
        System.exit(1);
    }

    public static void broadcastTimetableUpdate() {
        try {
            JsonObject timetableResponse = buildTimetableResponse();
            System.out.println("Broadcasting timetable update to " + clientWriters.size() + " clients");

            // Send to all connected clients
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println(timetableResponse.toString());
                    writer.flush();
                }
            }
        } catch (Exception e) {
            System.err.println("Error broadcasting update: " + e.getMessage());
        }
    }

    private static JsonObject buildTimetableResponse() {
        try {
            JsonArrayBuilder lecturesArrayBuilder = Json.createArrayBuilder();

            for (Lecture lecture : lectureList) {
                lecturesArrayBuilder.add(Json.createObjectBuilder()
                        .add("id", Integer.parseInt(String.valueOf(lecture.getId())))
                        .add("module", lecture.getModule().toString())
                        .add("lecturer", lecture.getLecturer())
                        .add("room", lecture.getRoom())
                        .add("fromTime", lecture.getFromTime())
                        .add("toTime", lecture.getToTime())
                        .add("day", lecture.getDay().toString())
                        .build());
            }

            return Json.createObjectBuilder()
                    .add("status", "success")
                    .add("Content-Type", "timetable")
                    .add("content", lecturesArrayBuilder.build())
                    .build();
        } catch (Exception e) {
            System.err.println("Error building timetable response: " + e.getMessage());
            return Json.createObjectBuilder()
                    .add("status", "error")
                    .add("Content-Type", "Exception")
                    .add("content", "Error building timetable response")
                    .build();
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
                dotenv.get("DB_NAME"));
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        return DriverManager.getConnection(url, user, password);
    }

    public static DB_instance getDataSource() {
        Dotenv dotenv = Dotenv.configure()
                .directory("Server/src/main/resources")
                .load();
        DB_instance ds = new DB_instance();
        ds.setUrl(String.format("jdbc:postgresql://%s:%s/%s",
                dotenv.get("DB_HOST"),
                dotenv.get("DB_PORT"),
                dotenv.get("DB_NAME")));
        ds.setUser(dotenv.get("DB_USER"));
        ds.setPassword(dotenv.get("DB_PASSWORD"));
        return ds;
    }

    private static void fillLectureList() throws SQLException {
        try {
            lectureList = dbManager.getLectures();
            App.fillLectureList();
        } catch (SQLException e) {
            // rethrown to be handled by caller
            throw e;
        }
    }

    public static DBManager getDatabaseManager() {
        return dbManager;
    }

    public ArrayList<Lecture> getLectureList() {
        return lectureList;
    }

    public static void setLectures(ArrayList<Lecture> l) {
        lectureList = l;
        broadcastTimetableUpdate();
    }

    static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private PrintWriter out;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            Socket link = clientSocket;
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
                PrintWriter out = new PrintWriter(link.getOutputStream(), true);
                this.out = out;

                System.out.println("Client connected: " + link.getInetAddress().getHostAddress());

                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                try {
                    String request;
                    while ((request = in.readLine()) != null) {
                        System.out.println("Received: " + request);

                        if (request.equals("STOP")) {
                            System.out.println("Client requested termination. Shutting down server.");
                            out.println("TERMINATE");
                            out.flush();
                            serverRunning = false;
                            try {
                                if (servSock != null && !servSock.isClosed()) {
                                    servSock.close();
                                }
                            } catch (IOException e) {
                                System.err.println("Error closing server socket: " + e.getMessage());
                            }
                            link.close();
                            break;
                        }

                        try {
                            StringReader stringReader = new StringReader(request);
                            JsonReader jsonReader = Json.createReader(stringReader);
                            JsonObject requestData = jsonReader.readObject();

                            String response;
                            boolean updateMade = false;

                            switch (requestData.getString("method")) {
                                case "GET" -> {
                                    Get get = new Get(requestData);
                                    response = get.responseBuilder();
                                }
                                case "POST" -> {
                                    Post post = new Post(requestData);
                                    response = post.responseBuilder();
                                    updateMade = true;
                                }
                                case "UPDATE" -> {
                                    Update update = new Update(requestData);
                                    response = update.responseBuilder();
                                    updateMade = true;
                                }
                                default -> throw new IncorrectActionException();
                            }

                            System.out.println("Sending: " + response);
                            out.println(response);
                            out.flush();

                            try {
                                if (updateMade) {
                                    fillLectureList();
                                    broadcastUpdateToOtherClients(out);
                                }
                            } catch (SQLException e) {
                                System.err.println("Error filling lecture list: " + e.getMessage());
                            }
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
                } finally {
                    synchronized (clientWriters) {
                        clientWriters.remove(out);
                    }

                    out.close();
                    in.close();
                    if (!link.isClosed()) {
                        try {
                            link.close();
                        } catch (IOException e) {
                            System.err.println("Error closing socket: " + e.getMessage());
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("IO Error: " + e.getMessage());
                if (this.out != null) {
                    synchronized (clientWriters) {
                        clientWriters.remove(this.out);
                    }
                }
            }
        }
    }

    private static void broadcastUpdateToOtherClients(PrintWriter excludedClient) {
        JsonObject timetableResponse = buildTimetableResponse();

        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                if (writer != excludedClient) {
                    writer.println(timetableResponse.toString());
                    writer.flush();
                }
            }
        }
    }
}