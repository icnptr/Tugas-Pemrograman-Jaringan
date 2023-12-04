import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ChatServer {
 private static final int PORT = 9876;

         public static void main(String[] args) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(PORT);
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData,
                        receiveData.length);

                 // Menerima pesan dari klien
                serverSocket.receive(receivePacket);

                 // Menampilkan pesan yang diterima
                 String message = new String(receivePacket.getData(), 0,
                        receivePacket.getLength());
                 System.out.println("Received message from " + receivePacket.getAddress() + ": " + message);

                 // Menyebarkan pesan ke semua klien yang terhubung (simulasi)
                 broadcastMessage(serverSocket, receivePacket);
                 }
             } catch (IOException e) {
             e.printStackTrace();
             }
         }
             private static void broadcastMessage(DatagramSocket serverSocket, DatagramPacket
                     receivePacket) throws IOException {
             // Simulasi: Menyebarkan pesan ke semua klien yang terhubung
                 // Untuk implementasi yang lebih canggih, simpan daftar klien yang terhubung
                  // dan kirim pesan ke setiap klien.
                  // Simulasi di sini hanya menunjukkan konsep dasar.
                  byte[] sendData = receivePacket.getData();
                  for (int port = 10000; port <= 10002; port++) {
                      serverSocket.send(new DatagramPacket(sendData, sendData.length,
                             receivePacket.getAddress(), port));
                      }
                  }
 }