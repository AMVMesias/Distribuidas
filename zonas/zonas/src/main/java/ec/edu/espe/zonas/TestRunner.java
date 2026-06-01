package ec.edu.espe.zonas;

import ec.edu.espe.zonas.dtos.ZonaRequestDto;
import ec.edu.espe.zonas.models.TipoZona;
import ec.edu.espe.zonas.servicio.ZonaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import org.jspecify.annotations.NonNull;

import java.util.Scanner;
import java.util.UUID;

@Component
public class TestRunner implements CommandLineRunner {

    @Autowired
    private ZonaServicio zonaServicio;

    @Override
    public void run(@NonNull String... args) {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        System.out.println("=================================================");
        System.out.println(" BIENVENIDO AL SISTEMA DE GESTION DE ZONAS");
        System.out.println("=================================================");

        while (continuar) {
            System.out.println("\n--- MENÚ PRINCIPAL ---");
            System.out.println("1. Crear nueva Zona");
            System.out.println("2. Listar todas las Zonas");
            System.out.println("3. Eliminar (Desactivar/Activar) Zona");
            System.out.println("4. Actualizar Zona");
            System.out.println("5. Salir");
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
                    eliminarZona(scanner);
                    break;
                case "4":
                    actualizarZonaInteractiva(scanner);
                    break;
                case "5":
                    continuar = false;
                    System.out.println("Saliendo del menú de pruebas...");
                    break;
                default:
                    System.out.println("ERROR: Opción no válida. Intenta de nuevo.");
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
            
            System.out.println("\nEXITO: Zona guardada correctamente en la Base de Datos!");
            System.out.println("   ID Asignado: " + respuesta.getId());
            System.out.println("   Código Generado: " + respuesta.getCodigo());

        } catch (IllegalArgumentException e) {
            System.out.println("\nERROR: El tipo de zona ingresado no es válido.");
        } catch (Exception e) {
            System.out.println("\nERROR al crear la zona: " + e.getMessage());
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
                    System.out.printf("- ID: %s | [%s] %s | Tipo: %s | Activa: %s%n", 
                            z.getId(), z.getCodigo(), z.getNombre(), z.getTipoZona(), z.isActive());
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR al listar las zonas: " + e.getMessage());
        }
    }

    private void eliminarZona(Scanner scanner) {
        System.out.println("\n-- ELIMINAR ZONA --");
        listarZonas();
        System.out.print("\nIngresa el ID de la zona a eliminar: ");
        String idStr = scanner.nextLine().trim();
        
        try {
            UUID id = UUID.fromString(idStr);
            boolean nuevoEstado = zonaServicio.activarDesactivar(id);
            if (!nuevoEstado) {
                System.out.println("EXITO: La zona ha sido ELIMINADA exitosamente (desactivada en el sistema).");
            } else {
                System.out.println("EXITO: La zona ha sido RESTAURADA exitosamente (vuelta a activar).");
            }
        } catch (IllegalArgumentException e) {
             System.out.println("ERROR: El formato del ID no es válido.");
        } catch (Exception e) {
             System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void actualizarZonaInteractiva(Scanner scanner) {
        System.out.println("\n-- ACTUALIZAR ZONA --");
        listarZonas();
        System.out.print("\nIngresa el ID de la zona a actualizar: ");
        String idStr = scanner.nextLine().trim();

        try {
            UUID id = UUID.fromString(idStr);

            System.out.print("Ingresa el nuevo nombre de la Zona: ");
            String nombre = scanner.nextLine();

            System.out.print("Ingresa la nueva descripción: ");
            String descripcion = scanner.nextLine();

            System.out.println("Tipos de Zona disponibles: VIP, REGULAR, INTERNA, EXTERNA, PREFERENCIAL");
            System.out.print("Ingresa el nuevo tipo de Zona: ");
            String tipoStr = scanner.nextLine().toUpperCase();
            
            TipoZona tipoZona = TipoZona.valueOf(tipoStr);

            ZonaRequestDto dto = ZonaRequestDto.builder()
                    .nombre(nombre)
                    .descripcion(descripcion)
                    .tipo(tipoZona)
                    .build();

            var respuesta = zonaServicio.actualizarZona(id, dto);

            System.out.println("\nEXITO: Zona actualizada correctamente!");
            System.out.println("   ID: " + respuesta.getId());
            System.out.println("   Nombre: " + respuesta.getNombre());
            System.out.println("   Descripción: " + respuesta.getDescripcion());
            System.out.println("   Tipo: " + respuesta.getTipoZona());

        } catch (IllegalArgumentException e) {
            System.out.println("\nERROR: Formato de ID o Tipo de Zona no es válido.");
        } catch (Exception e) {
            System.out.println("\nERROR al actualizar la zona: " + e.getMessage());
        }
    }
}
