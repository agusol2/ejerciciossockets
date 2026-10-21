  System.out.println("Cliente conectado.");

                String mensaje;

                while ((mensaje = entrada.readLine()) != null) {
                    if ("CHAO".equalsIgnoreCase(mensaje)) {
                        break;
                    }

                    salida.println("ECO: " + mensaje);
                    salida.flush();
                }

                System.out.println("Cliente desconectado.");
            }

        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }
}