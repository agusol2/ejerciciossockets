
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RoomManager {

    private final Map<String, List<ClientHandler>> salas =
            new ConcurrentHashMap<>();

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
}
