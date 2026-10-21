
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (Socket cliente = socket;
             BufferedReader entrada = new BufferedReader(
                 new InputStreamReader(cliente.getInputStream()));
             PrintWriter salida = new PrintWriter(
                 cliente.getOutputStream(), true)) {

            salida.println("Conectado al servidor multihilo.");

            String mensaje;

            while ((mensaje = entrada.readLine()) != null) {
                if ("CHAO".equalsIgnoreCase(mensaje)) {
                    break;
                }

                salida.println("ECO: " + mensaje);
                salida.flush();
            }

        } catch (IOException e) {
            System.out.println(
                "Cliente desconectado: " + e.getMessage()
            );
        }
    }
}
