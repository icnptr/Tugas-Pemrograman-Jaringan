import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NonBlockingClient {
    private SocketChannel socketChannel;

    public NonBlockingClient(String serverAddress, int serverPort, String clientId) {
        try {
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(serverAddress, serverPort));
            socketChannel.configureBlocking(false);

            // Send client ID to server
            socketChannel.write(ByteBuffer.wrap(clientId.getBytes()));

            System.out.println("Connected to the server");
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
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    while (true) {
                        int bytesRead = socketChannel.read(buffer);
                        if (bytesRead > 0) {
                            buffer.flip();
                            String serverMessage = new String(buffer.array(), 0, bytesRead);
                            System.out.println("Server: " + serverMessage);
                            buffer.clear();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Send messages to the server
            String userInputMessage;
            while ((userInputMessage = userInput.readLine()) != null) {
                socketChannel.write(ByteBuffer.wrap(userInputMessage.getBytes()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.print("Enter your client ID: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String clientId = reader.readLine();
            NonBlockingClient client = new NonBlockingClient("localhost", 1234, clientId);
            client.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
