package ul.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

// nc localhost 8080

public class App {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        try (ServerSocket servSock = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT);
            boolean run = true;
            while (run) {
                Socket link = null;
                try {
                    link = servSock.accept();
                    System.out.println("New connection from " + link.getInetAddress().getHostAddress());

                    BufferedReader in =
                            new BufferedReader(new InputStreamReader(link.getInputStream()));
                    PrintWriter out = new PrintWriter(link.getOutputStream(), true);

                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println("Received: " + message);
                        if (message.equals("CLOSE")) {
                            out.println("TERMINATE");
                            link.close();
                            run = false;
                            break;
                        } else {
                            out.println("Server: " + message);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (link != null && !link.isClosed()) {
                            link.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}