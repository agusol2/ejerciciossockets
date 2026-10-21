
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

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

    public void cerrarConexion() {
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Error al cerrar una conexion.");
        }
    }

    @Override
    public void run() {
        roomManager.registrarCliente(this);

        try (Socket cliente = socket) {
            cliente.setSoTimeout(30000);
            cliente.setKeepAlive(true);

            BufferedReader entrada = new BufferedReader(
                new InputStreamReader(cliente.getInputStream())
            );

            salida = new PrintWriter(
                cliente.getOutputStream(), true
            );

            salida.println("Bienvenido al chat JSMS.");
            salida.println("Usa CONNECT usuario para ingresar.");

            String linea;

            while ((linea = entrada.readLine()) != null) {
                try {
                    Command comando = ProtocolParser.parse(linea);

                    switch (comando.getTipo()) {
                        case "CONNECT":
                            if (usuario != null) {
                                salida.println("ERROR Ya estas conectado.");
                            } else if (!roomManager.registrarUsuario(
                                    comando.getArgumento())) {
                                salida.println("401 ERROR Usuario ocupado.");
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

        } catch (SocketTimeoutException e) {
            System.out.println("Cliente inactivo: timeout.");

        } catch (SocketException e) {
            System.out.println("Conexion interrumpida.");

        } catch (IOException e) {
            System.out.println("Error de comunicacion: " + e.getMessage());

        } finally {
            roomManager.salirDeSala(salaActual, this);
            roomManager.liberarUsuario(usuario);
            roomManager.eliminarCliente(this);
        }
    }
}
