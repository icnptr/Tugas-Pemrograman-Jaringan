import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BlockingClient {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public BlockingClient(String serverAddress, int serverPort, String clientId) {
        try {
            socket = new Socket(serverAddress, serverPort);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            // Send client ID to server
            writer.println(clientId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            // Read messages from the server
            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = reader.readLine()) != null) {
                        System.out.println("Server: " + serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Send messages to the server
            String userInputMessage;
            while ((userInputMessage = userInput.readLine()) != null) {
                writer.println(userInputMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        System.out.print("Enter your client ID: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String clientId = reader.readLine();
            BlockingClient client = new BlockingClient("localhost", 8080, clientId);
            client.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
