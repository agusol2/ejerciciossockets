
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultiClientServer {

    private static final int PUERTO = 8080;

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        RoomManager roomManager = new RoomManager();

        final ServerSocket servidor;

        try {
            servidor = new ServerSocket(PUERTO);
        } catch (IOException e) {
            System.out.println("No se pudo iniciar el servidor.");
            pool.shutdownNow();
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Apagando servidor...");

            roomManager.notificarApagado();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            try {
                servidor.close();
            } catch (IOException e) {
                System.out.println("Error al cerrar el servidor.");
            }

            roomManager.cerrarClientes();
            pool.shutdown();

            try {
                if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                    pool.shutdownNow();
                }
            } catch (InterruptedException e) {
                pool.shutdownNow();
                Thread.currentThread().interrupt();
            }

            System.out.println("Servidor apagado.");
        }));

        System.out.println("Servidor JSMS iniciado en puerto " + PUERTO);

        try {
            while (!servidor.isClosed()) {
                Socket socket = servidor.accept();

                ClientHandler cliente =
                        new ClientHandler(socket, roomManager);

                pool.execute(cliente);
            }

        } catch (IOException e) {
            if (!servidor.isClosed()) {
                System.out.println("Error del servidor: " + e.getMessage());
            }
        } finally {
            pool.shutdown();
        }
    }
}
