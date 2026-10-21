
public class ProtocolParser {

    public static Command parse(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("Comando vacio.");
        }

        texto = texto.trim();

        if (texto.equalsIgnoreCase("QUIT")) {
            return new Command("QUIT", "");
        }

        if (texto.regionMatches(true, 0, "CONNECT ", 0, 8)) {
            String usuario = texto.substring(8).trim();

            if (usuario.isEmpty() || usuario.contains(" ")) {
                throw new IllegalArgumentException("Usuario invalido.");
            }

            return new Command("CONNECT", usuario);
        }

        if (texto.regionMatches(true, 0, "JOIN ", 0, 5)) {
            String sala = texto.substring(5).trim();

            if (sala.isEmpty() || sala.contains(" ")) {
                throw new IllegalArgumentException("Sala invalida.");
            }

            return new Command("JOIN", sala);
        }

        if (texto.regionMatches(true, 0, "MSG ", 0, 4)) {
            String mensaje = texto.substring(4).trim();

            if (mensaje.isEmpty()) {
                throw new IllegalArgumentException("Mensaje vacio.");
            }

            return new Command("MSG", mensaje);
        }

        throw new IllegalArgumentException("Comando desconocido.");
    }
}
