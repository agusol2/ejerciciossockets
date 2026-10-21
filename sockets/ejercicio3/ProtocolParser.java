
public class ProtocolParser {

    public static Command parse(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El comando esta vacio."
            );
        }

        texto = texto.trim();

        if (texto.equalsIgnoreCase("QUIT")) {
            return new Command("QUIT", "");
        }

        if (texto.regionMatches(true, 0, "CONNECT ", 0, 8)) {
            String usuario = texto.substring(8).trim();

            if (usuario.isEmpty() || usuario.contains(" ")) {
                throw new IllegalArgumentException(
                    "CONNECT requiere un nombre de usuario valido."
                );
            }

            return new Command("CONNECT", usuario);
        }

        if (texto.regionMatches(true, 0, "JOIN ", 0, 5)) {
            String sala = texto.substring(5).trim();

            if (sala.isEmpty() || sala.contains(" ")) {
                throw new IllegalArgumentException(
                    "JOIN requiere un nombre de sala valido."
                );
            }

            return new Command("JOIN", sala);
        }

        if (texto.regionMatches(true, 0, "MSG ", 0, 4)) {
            String mensaje = texto.substring(4).trim();

            if (mensaje.isEmpty()) {
                throw new IllegalArgumentException(
                    "MSG requiere un mensaje."
                );
            }

            return new Command("MSG", mensaje);
        }

        throw new IllegalArgumentException(
            "Comando desconocido o sintaxis incorrecta."
        );
    }
}
