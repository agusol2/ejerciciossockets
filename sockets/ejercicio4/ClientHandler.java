
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final RoomManager roomManager;

    private PrintWriter salida;
    private String usuario;
    private String salaActual;

    public ClientHandler(Socket socket, RoomManager roomManager) {
        this.socket = socket;
        this.roomManager = roomManager;
    }

    public synchronized void enviarMensajeDirecto(String mensaje) {
        if (salida != null) {
            salida.println(mensaje);
        }
    }

    @Override
    public void run() {
        try (Socket cliente = socket;
             BufferedReader entrada = new BufferedReader(
                 new InputStreamReader(cliente.getInputStream()));
             PrintWriter out = new PrintWriter(
                 cliente.getOutputStream(), true)) {

            salida = out;
            salida.println("Bienvenido al chat.");

            String linea;

            while ((linea = entrada.readLine()) != null) {
                try {
                    Command comando = ProtocolParser.parse(linea);

                    switch (comando.getTipo()) {
                        case "CONNECT":
                            if (usuario != null) {
                                salida.println("ERROR Ya estas conectado.");
                            } else {
                                usuario = comando.getArgumento();
                                salida.println("200 OK " + usuario);
                            }
                            break;

                        case "JOIN":
                            if (usuario == null) {
                                salida.println("ERROR Usa CONNECT primero.");
                                break;
                            }

                            roomManager.salirDeSala(salaActual, this);
                            salaActual = comando.getArgumento();
                            roomManager.unirseASala(salaActual, this);

                            salida.println("JOINED " + salaActual);
                            break;

                        case "MSG":
                            if (usuario == null || salaActual == null) {
                                salida.println(
                                    "ERROR Conectate y entra a una sala."
                                );
                                break;
                            }

                            roomManager.broadcast(
                                salaActual,
                                "BROADCAST [" + usuario + "]: "
                                    + comando.getArgumento(),
                                this
                            );

                            salida.println("200 Mensaje enviado.");
                            break;

                        case "QUIT":
                            salida.println("Sesion finalizada.");
                            return;

                        default:
                            salida.println("ERROR Comando desconocido.");
                    }

                } catch (IllegalArgumentException e) {
                    salida.println("ERROR " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("Cliente desconectado.");
        } finally {
            roomManager.salirDeSala(salaActual, this);
        }
    }
}
