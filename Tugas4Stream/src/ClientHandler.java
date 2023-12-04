import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
                InputStream inputStream = clientSocket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)
        ) {
            // Menerima detail file dari client
            String namaFile = objectInputStream.readUTF();
            long ukuranFile = objectInputStream.readLong();

            byte[] byteFile = new byte[(int) ukuranFile];
            objectInputStream.readFully(byteFile);

            // Menyimpan file di server (sesuaikan path)
            String pathServer = "path/to/save/" + namaFile;
            try (FileOutputStream fileOutputStream = new FileOutputStream(pathServer)) {
                fileOutputStream.write(byteFile);
            }

            // Memberi informasi ke client bahwa file berhasil diterima
            try (OutputStream outputStream = clientSocket.getOutputStream();
                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
                objectOutputStream.writeUTF("File diterima dengan sukses: " + namaFile);
                objectOutputStream.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Tutup koneksi setelah selesai
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
