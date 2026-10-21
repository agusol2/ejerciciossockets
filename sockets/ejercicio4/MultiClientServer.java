
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiClientServer {

    private static final int PUERTO = 8080;

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        RoomManager roomManager = new RoomManager();

        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor de chat iniciado en " + PUERTO);

            while (true) {
                Socket socket = servidor.accept();

                pool.execute(
                    new ClientHandler(socket, roomManager)
                );
            }

        } catch (IOException e) {
            System.out.println("Error del servidor: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }
}
