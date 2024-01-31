import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CombinedMultiThreadedServer {

    private static Map<String, PrintWriter> clientsMap = new ConcurrentHashMap<>();
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server is running...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                executorService.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter writer;
        private String clientName;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                this.writer = writer;

                writer.println("Masukkan nama Anda:");
                Scanner scanner = new Scanner(clientSocket.getInputStream());
                if (scanner.hasNextLine()) {
                    this.clientName = scanner.nextLine();
                    System.out.println("Client " + clientName + " identified.");

                    clientsMap.put(clientName, writer);
                    broadcastMessage(clientName + " bergabung.");

                    while (true) {
                        if (scanner.hasNextLine()) {
                            String message = scanner.nextLine();
                            if ("exit".equalsIgnoreCase(message)) {
                                break;
                            }

                            // Menangani pesan khusus untuk mengirim pesan pribadi
                            if (message.startsWith("/private ")) {
                                String[] parts = message.split(" ", 3);
                                if (parts.length == 3) {
                                    String recipient = parts[1];
                                    String privateMessage = parts[2];
                                    sendMessage(recipient, clientName + " (private): " + privateMessage);
                                }
                            } else {
                                broadcastMessage(clientName + ": " + message);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (clientName != null) {
                    clientsMap.remove(clientName);
                    broadcastMessage(clientName + " keluar.");
                }
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void sendMessage(String recipient, String message) {
            PrintWriter recipientWriter = clientsMap.get(recipient);
            if (recipientWriter != null) {
                recipientWriter.println(message);
            } else {
                System.out.println("Klien " + recipient + " tidak ditemukan.");
            }
        }

        private void broadcastMessage(String message) {
            for (PrintWriter client : clientsMap.values()) {
                client.println(message);
            }
        }
    }
}
