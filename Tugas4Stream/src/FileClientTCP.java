import java.io.*;
import java.net.Socket;

public class FileClientTCP {
    public static void main(String[] args) {
        String alamatServer = "localhost";
        int portNumber = 12345;
        String pathFile = "path/to/your/file.txt";

        try (Socket socket = new Socket(alamatServer, portNumber);
             FileInputStream fileInputStream = new FileInputStream(pathFile);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {

            // Mengirim detail file ke server
            File file = new File(pathFile);
            objectOutputStream.writeUTF(file.getName());
            objectOutputStream.writeLong(file.length());
            objectOutputStream.flush();

            // Mengirim konten file ke server
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                objectOutputStream.write(buffer, 0, bytesRead);
            }
            objectOutputStream.flush();

            // Menerima konfirmasi dari server
            try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {
                String konfirmasi = objectInputStream.readUTF();
                System.out.println(konfirmasi);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
