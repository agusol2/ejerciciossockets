
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RoomManager {

    private final Map<String, List<ClientHandler>> salas =
            new ConcurrentHashMap<>();

    private final Set<String> usuarios =
            ConcurrentHashMap.newKeySet();

    private final Set<ClientHandler> clientes =
            ConcurrentHashMap.newKeySet();

    public boolean registrarUsuario(String usuario) {
        return usuarios.add(usuario.toLowerCase());
    }

    public void liberarUsuario(String usuario) {
        if (usuario != null) {
            usuarios.remove(usuario.toLowerCase());
        }
    }

    public void registrarCliente(ClientHandler cliente) {
        clientes.add(cliente);
    }

    public void eliminarCliente(ClientHandler cliente) {
        clientes.remove(cliente);
    }

    public void unirseASala(String sala, ClientHandler cliente) {
        salas.computeIfAbsent(
            sala,
            nombre -> new CopyOnWriteArrayList<>()
        ).addIfAbsent(cliente);
    }

    public void salirDeSala(String sala, ClientHandler cliente) {
        if (sala == null) {
            return;
        }

        List<ClientHandler> miembros = salas.get(sala);

        if (miembros != null) {
            miembros.remove(cliente);

            if (miembros.isEmpty()) {
                salas.remove(sala, miembros);
            }
        }
    }

    public void broadcast(
            String sala,
            String mensaje,
            ClientHandler emisor) {

        List<ClientHandler> miembros = salas.get(sala);

        if (miembros != null) {
            for (ClientHandler miembro : miembros) {
                if (miembro != emisor) {
                    miembro.enviarMensajeDirecto(mensaje);
                }
            }
        }
    }

    public void notificarApagado() {
        for (ClientHandler cliente : clientes) {
            cliente.enviarMensajeDirecto(
                "SERVIDOR: El servidor se esta apagando."
            );
        }
    }

    public void cerrarClientes() {
        for (ClientHandler cliente : clientes) {
            cliente.cerrarConexion();
        }
    }
}
