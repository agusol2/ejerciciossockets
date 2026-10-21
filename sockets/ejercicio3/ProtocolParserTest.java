
import java.util.Scanner;

public class ProtocolParserTest {

    public static void main(String[] args) {
        try (Scanner teclado = new Scanner(System.in)) {
            System.out.println("Analizador de comandos");
            System.out.println("Ejemplos:");
            System.out.println("CONNECT Juan");
            System.out.println("JOIN general");
            System.out.println("MSG Hola a todos");
            System.out.println("QUIT");

            while (true) {
                System.out.print("> ");
                String texto = teclado.nextLine();

                try {
                    Command comando = ProtocolParser.parse(texto);

                    System.out.println("Tipo: " + comando.getTipo());
                    System.out.println(
                        "Argumento: " + comando.getArgumento()
                    );

                    if (comando.getTipo().equals("QUIT")) {
                        break;
                    }

                } catch (IllegalArgumentException e) {
                    System.out.println("ERROR: " + e.getMessage());
                }
            }
        }
    }
}
