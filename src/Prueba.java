import model.Administrador;

public class Prueba {
    public static void main(String[] args) {
        System.out.println("=== INICIANDO PRUEBA DE LOGS ===");

        // 1. Creamos una instancia usando el constructor vacío (el cual tiene nuestro logger.info)
        Administrador admin1 = new Administrador();

        // 2. Modificamos algunos datos para simular su uso
        admin1.setIdAdministrador(1);
        admin1.setNombres("Anthony Palomino");
        admin1.setCorreo("anthony@correo.com");

        // 3. Mostramos un mensaje final en la consola normal
        System.out.println("Administrador creado con éxito: " + admin1.getNombres());
        System.out.println("=== FIN DE LA PRUEBA ===");
    }
}