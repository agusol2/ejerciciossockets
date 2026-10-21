
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiClientServer {

    private static final int PUERTO = 8080;
    private static final ExecutorService pool =
            Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor multihilo iniciado.");
            System.out.println("Esperando clientes...");

            while (true) {
                Socket socket = servidor.accept();

                System.out.println("Nuevo cliente conectado.");

                pool.execute(new ClientHandler(socket));
            }

        } catch (IOException e) {
            System.out.println("Error del servidor: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }
}
