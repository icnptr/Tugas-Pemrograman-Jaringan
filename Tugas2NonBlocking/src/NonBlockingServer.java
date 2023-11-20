import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NonBlockingServer {
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private ConcurrentHashMap<SocketChannel, String> clients;

    public NonBlockingServer(int port) {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);

            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            clients = new ConcurrentHashMap<>();

            System.out.println("Server is running on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            while (true) {
                selector.select();

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if (key.isAcceptable()) {
                        handleAccept(key);
                    } else if (key.isReadable()) {
                        handleRead(key);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);

        clients.put(clientChannel, "");
        System.out.println("New client connected: " + clientChannel);
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = clientChannel.read(buffer);

        if (bytesRead == -1) {
            // Client disconnected
            String clientId = clients.remove(clientChannel);
            clientChannel.close();
            System.out.println("Client disconnected: " + clientId);
        } else {
            // Process incoming message
            buffer.flip();
            String message = new String(buffer.array(), 0, bytesRead);
            String clientId = clients.get(clientChannel);

            if (clientId.isEmpty()) {
                // First message contains the client ID
                clients.put(clientChannel, message);
                System.out.println("Client identified: " + message);
            } else {
                // Regular message
                System.out.println("Message from " + clientId + ": " + message);

                // Broadcast message to all clients
                broadcastMessage(clientChannel, clientId + ": " + message);
            }
        }
    }

    private void broadcastMessage(SocketChannel sender, String message) throws IOException {
        for (SocketChannel clientChannel : clients.keySet()) {
            if (clientChannel != sender) {
                clientChannel.write(ByteBuffer.wrap(message.getBytes()));
            }
        }
    }

    public static void main(String[] args) {
        NonBlockingServer server = new NonBlockingServer(1234);
        server.start();
    }
}
