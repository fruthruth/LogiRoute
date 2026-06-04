// 1. IMPORTAMOS TODOS LOS MODELOS DEL PAQUETE
import model.Administrador;
import model.Cliente;
import model.Pedido;
import model.Repartidor;
import model.Ruta;
import model.Vehiculo;

public class Prueba {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("=== INICIANDO PRUEBA GLOBAL DE LOGS LOGIROUTE ===");
        System.out.println("=================================================");

        // --- PRUEBA 1: ADMINISTRADOR ---
        System.out.println("\n[Probando Administrador...]");
        Administrador admin = new Administrador();
        admin.setIdAdministrador(1);
        admin.setNombres("Anthony Palomino");
        System.out.println(">> OK: " + admin.getNombres());

        // --- PRUEBA 2: CLIENTE ---
        System.out.println("\n[Probando Cliente...]");
        Cliente cliente = new Cliente();
        cliente.setNombre("Carlos Mendoza");
        System.out.println(">> OK: " + cliente.getNombre());

        // --- PRUEBA 3: REPARTIDOR ---
        System.out.println("\n[Probando Repartidor...]");
        Repartidor repartidor = new Repartidor();
        // Nota: Ajusta los setters según los nombres exactos que tengan tus atributos
        System.out.println(">> OK: Repartidor instanciado.");

        // --- PRUEBA 4: VEHICULO ---
        System.out.println("\n[Probando Vehiculo...]");
        Vehiculo vehiculo = new Vehiculo();
        System.out.println(">> OK: Vehiculo instanciado.");

        // --- PRUEBA 5: RUTA ---
        System.out.println("\n[Probando Ruta...]");
        Ruta ruta = new Ruta();
        System.out.println(">> OK: Ruta instanciada.");

        // --- PRUEBA 6: PEDIDO ---
        System.out.println("\n[Probando Pedido...]");
        Pedido pedido = new Pedido();
        System.out.println(">> OK: Pedido instanciado.");

        System.out.println("\n=================================================");
        System.out.println("===      FIN DE LA PRUEBA - TODO OK           ===");
        System.out.println("=================================================");
    }
}