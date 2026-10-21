
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoClient {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8080);
             BufferedReader teclado = new BufferedReader(
                 new InputStreamReader(System.in));
             BufferedReader entrada = new BufferedReader(
                 new InputStreamReader(socket.getInputStream()));
             PrintWriter salida = new PrintWriter(
                 socket.getOutputStream(), true)) {

            System.out.println("Conectado al servidor.");
            System.out.println("Escribi un mensaje o CHAO para salir.");

            String mensaje;

            while ((mensaje = teclado.readLine()) != null) {
                salida.println(mensaje);
                salida.flush();

                if ("CHAO".equalsIgnoreCase(mensaje)) {
                    break;
                }

                String respuesta = entrada.readLine();

                if (respuesta == null) {
                    System.out.println("El servidor cerro la conexion.");
                    break;
                }

                System.out.println("Respuesta: " + respuesta);
            }

        } catch (IOException e) {
            System.out.println("Error en el cliente: " + e.getMessage());
        }
    }
}
