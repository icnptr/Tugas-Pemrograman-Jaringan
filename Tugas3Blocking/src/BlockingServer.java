import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class BlockingServer {
    private ServerSocket serverSocket;
    private Map<String, PrintWriter> clients;

    public BlockingServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            clients = new HashMap<>();
            System.out.println("Server is running on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

                String clientId = reader.readLine();
                clients.put(clientId, writer);

                new Thread(() -> handleClient(clientSocket, clientId)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket, String clientId) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String message;

            while ((message = reader.readLine()) != null) {
                System.out.println("Message from " + clientId + ": " + message);

                // Broadcast message to all clients
                broadcastMessage(clientId + ": " + message);
            }

            // Client disconnected
            System.out.println("Client disconnected: " + clientId);
            clients.remove(clientId);
            broadcastMessage(clientId + " has left the chat.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcastMessage(String message) {
        for (PrintWriter clientWriter : clients.values()) {
            clientWriter.println(message);
        }
    }

    public static void main(String[] args) {
        BlockingServer server = new BlockingServer(8080);
        server.start();
    }
}
