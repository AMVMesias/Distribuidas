package ec.edu.espe.zonas;

import ec.edu.espe.zonas.dtos.ZonaRequestDto;
import ec.edu.espe.zonas.models.TipoZona;
import ec.edu.espe.zonas.servicio.ZonaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class TestRunner implements CommandLineRunner {

    @Autowired
    private ZonaServicio zonaServicio;

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        System.out.println("=================================================");
        System.out.println(" BIENVENIDO AL SISTEMA DE GESTION DE ZONAS");
        System.out.println("=================================================");

        while (continuar) {
            System.out.println("\n--- MENÚ PRINCIPAL ---");
            System.out.println("1. Crear nueva Zona");
            System.out.println("2. Listar todas las Zonas");
            System.out.println("3. Salir");
            System.out.print("Elige una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    crearZonaInteractiva(scanner);
                    break;
                case "2":
                    listarZonas();
                    break;
                case "3":
                    continuar = false;
                    System.out.println("Saliendo del menú de pruebas...");
                    break;
                default:
                    System.out.println("❌ Opción no válida. Intenta de nuevo.");
            }
        }
    }

    private void crearZonaInteractiva(Scanner scanner) {
        try {
            System.out.println("\n-- CREAR NUEVA ZONA --");
            
            System.out.print("Ingresa el nombre de la Zona: ");
            String nombre = scanner.nextLine();

            System.out.print("Ingresa una descripción: ");
            String descripcion = scanner.nextLine();

            System.out.println("Tipos de Zona disponibles: VIP, REGULAR, INTERNA, EXTERNA, PREFERENCIAL");
            System.out.print("Ingresa el tipo de Zona: ");
            String tipoStr = scanner.nextLine().toUpperCase();
            
            TipoZona tipoZona = TipoZona.valueOf(tipoStr);

            ZonaRequestDto nuevaZona = ZonaRequestDto.builder()
                    .nombre(nombre)
                    .descripcion(descripcion)
                    .tipo(tipoZona)
                    .build();

            var respuesta = zonaServicio.crearZona(nuevaZona);
            
            System.out.println("\n✅ EXITO: Zona guardada correctamente en la Base de Datos!");
            System.out.println("   ID Asignado: " + respuesta.getId());
            System.out.println("   Código Generado: " + respuesta.getCodigo());

        } catch (IllegalArgumentException e) {
            System.out.println("\n❌ ERROR: El tipo de zona ingresado no es válido.");
        } catch (Exception e) {
            System.out.println("\n❌ ERROR al crear la zona: " + e.getMessage());
        }
    }

    private void listarZonas() {
        System.out.println("\n-- ZONAS REGISTRADAS --");
        try {
            var zonas = zonaServicio.listarZonas();
            if (zonas.isEmpty()) {
                System.out.println("No hay zonas registradas en la base de datos.");
            } else {
                for (var z : zonas) {
                    System.out.println(String.format("- [%s] %s | Tipo: %s | Activa: %s", 
                            z.getCodigo(), z.getNombre(), z.getTipoZona(), z.isActive()));
                }
            }
        } catch (Exception e) {
            System.out.println("❌ ERROR al listar las zonas: " + e.getMessage());
        }
    }
}
