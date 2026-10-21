
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8080);
             BufferedReader teclado = new BufferedReader(
                 new InputStreamReader(System.in));
             BufferedReader entrada = new BufferedReader(
                 new InputStreamReader(socket.getInputStream()));
             PrintWriter salida = new PrintWriter(
                 socket.getOutputStream(), true)) {

            Thread receptor = new Thread(() -> {
                try {
                    String respuesta;

                    while ((respuesta = entrada.readLine()) != null) {
                        System.out.println(respuesta);
                    }

                    System.out.println("Conexion cerrada por el servidor.");

                } catch (IOException e) {
                    System.out.println("Se cerro la conexion.");
                }
            });

            receptor.setDaemon(true);
            receptor.start();

            System.out.println("Comandos disponibles:");
            System.out.println("CONNECT Juan");
            System.out.println("JOIN general");
            System.out.println("MSG Hola a todos");
            System.out.println("QUIT");

            String mensaje;

            while ((mensaje = teclado.readLine()) != null) {
                salida.println(mensaje);
                salida.flush();

                if ("QUIT".equalsIgnoreCase(mensaje.trim())) {
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("No se pudo conectar: " + e.getMessage());
        }
    }
}
