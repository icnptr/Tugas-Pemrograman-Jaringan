import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ChatClient {
private static final String SERVER_IP = "localhost";
private static final int SERVER_PORT = 9876;

        public static void main(String[] args) {
        try {
            DatagramSocket clientSocket = new DatagramSocket();

             // Memasukkan nama klien
             System.out.print("Enter your name: ");
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
             String name = reader.readLine();

             // Mengirim pesan ke server
             sendMessage(clientSocket, name);

             // Menutup socket setelah mengirim pesan
             clientSocket.close();
             } catch (IOException e) {
             e.printStackTrace();
             }
         }

         private static void sendMessage(DatagramSocket clientSocket, String name) throws
            IOException {
         BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

         while (true) {
             // Memasukkan pesan
             System.out.print("Enter your message (or type 'exit' to quit): ");
             String message = reader.readLine();

             if ("exit".equalsIgnoreCase(message)) {
            }

             // Mengirim pesan ke server
             byte[] sendData = (name + ": " + message).getBytes();
             DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                    InetAddress.getByName(SERVER_IP), SERVER_PORT);
             clientSocket.send(sendPacket);
             }
         }}