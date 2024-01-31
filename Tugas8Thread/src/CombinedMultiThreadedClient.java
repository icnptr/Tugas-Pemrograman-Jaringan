import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CombinedMultiThreadedClient {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8080)) {
            System.out.println("Terhubung ke server...");

            ExecutorService executorService = Executors.newFixedThreadPool(2);

            // Thread untuk membaca pesan dari server
            executorService.submit(new InputHandler(socket));

            // Thread untuk mengirim pesan ke server
            executorService.submit(new OutputHandler(socket));

            // Tunggu hingga kedua thread selesai
            executorService.shutdown();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class InputHandler implements Runnable {
        private Socket socket;

        public InputHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (Scanner scanner = new Scanner(socket.getInputStream())) {
                while (scanner.hasNextLine()) {
                    String message = scanner.nextLine();
                    System.out.println(message);
                }
            } catch (IOException e) {
                // Tangani pengecualian, misalnya socket ditutup secara tidak terduga
                e.printStackTrace();
            }
        }
    }

    static class OutputHandler implements Runnable {
        private Socket socket;

        public OutputHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                 Scanner scanner = new Scanner(System.in)) {

                System.out.println("Masukkan nama Anda:");
                String clientName = scanner.nextLine();
                writer.println(clientName);

                while (true) {
                    String message = scanner.nextLine();
                    writer.println(message);
                    if ("exit".equalsIgnoreCase(message)) {
                        break;
                    }
                }

            } catch (IOException e) {
                // Tangani pengecualian, misalnya socket ditutup secara tidak terduga
                e.printStackTrace();
            }
        }
    }
}
