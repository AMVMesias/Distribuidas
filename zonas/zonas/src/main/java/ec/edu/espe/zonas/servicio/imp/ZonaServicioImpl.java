package ec.edu.espe.zonas.servicio.imp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import ec.edu.espe.zonas.dtos.ZonaRequestDto;
import ec.edu.espe.zonas.dtos.ZonaResponseDto;
import ec.edu.espe.zonas.models.Zona;
import ec.edu.espe.zonas.models.TipoZona;
import ec.edu.espe.zonas.repositories.ZonaRepositorio;
import ec.edu.espe.zonas.servicio.ZonaServicio;

@Service
public class ZonaServicioImpl implements ZonaServicio {
    @Autowired
    private ZonaRepositorio zonaRepositorio;

    @Override
    public List<ZonaResponseDto> listarZonas() {
        return zonaRepositorio.findAll()
                .stream()
                .filter(Zona::isActive)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ZonaResponseDto crearZona(ZonaRequestDto zona) {
        // Validamos en memoria que no exista otra zona ACTIVA con el mismo nombre
        boolean existeNombreActivo = zonaRepositorio.findAll().stream()
                .anyMatch(z -> z.getNombre().equalsIgnoreCase(zona.getNombre()) && z.isActive());
        if (existeNombreActivo) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una zona activa con el mismo nombre");
        }

        String codigo = generarCodigoZona(zona.getNombre(), zona.getTipo());
        // Validamos en memoria que no exista otra zona ACTIVA con el mismo código
        boolean existeCodigoActivo = zonaRepositorio.findAll().stream()
                .anyMatch(z -> z.getCodigo().equals(codigo) && z.isActive());
        if (existeCodigoActivo) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una zona activa con el mismo código: " + codigo);
        }

        Zona objZona = new Zona();

        objZona.setNombre(zona.getNombre());
        objZona.setDescripcion(zona.getDescripcion());
        objZona.setCodigo(codigo);
        objZona.setActive(true);
        objZona.setTipoZona(zona.getTipo());
        objZona.setFechaCreacion(java.time.LocalDateTime.now());
        objZona.setFechaActualizacion(java.time.LocalDateTime.now());
        zonaRepositorio.save(objZona);

        return toResponse(objZona);
    }

    @Override
    public ZonaResponseDto actualizarZona(UUID idZona, ZonaRequestDto zona) {
        Zona objZona = zonaRepositorio.findById(idZona)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Zona no encontrada"));

        if (!objZona.getNombre().equalsIgnoreCase(zona.getNombre())) {
            boolean existeNombreActivo = zonaRepositorio.findAll().stream()
                    .anyMatch(z -> z.getNombre().equalsIgnoreCase(zona.getNombre()) && z.isActive());
            if (existeNombreActivo) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una zona activa con el mismo nombre");
            }
        }

        objZona.setNombre(zona.getNombre());
        objZona.setDescripcion(zona.getDescripcion());
        objZona.setTipoZona(zona.getTipo());
        objZona.setFechaActualizacion(java.time.LocalDateTime.now());
        zonaRepositorio.save(objZona);

        return toResponse(objZona);
    }

    @Override
    public boolean activarDesactivar(UUID idZona) {
        Zona objZona = zonaRepositorio.findById(idZona)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Zona no encontrada"));
        objZona.setActive(!objZona.isActive());
        objZona.setFechaActualizacion(java.time.LocalDateTime.now());
        zonaRepositorio.save(objZona);
        return objZona.isActive();
    }

    private ZonaResponseDto toResponse(Zona objZona) {
        return ZonaResponseDto.builder()
                .id(objZona.getId())
                .codigo(objZona.getCodigo())
                .nombre(objZona.getNombre())
                .descripcion(objZona.getDescripcion())
                .active(objZona.isActive())
                .tipoZona(objZona.getTipoZona())
                .espacios(objZona.getEspacios())
                .fechaCreacion(objZona.getFechaCreacion())
                .build();
    }

    private String generarCodigoZona(String nombre, TipoZona tipoZona) {
        String inicialNombre = nombre.substring(0, 1).toUpperCase();
        String inicialTipo = tipoZona.name().substring(0, 1).toUpperCase();
        
        List<Zona> todasLasZonas = zonaRepositorio.findAll();
        
        // Secuenciales basados únicamente en zonas activas
        long secuencialTipo = todasLasZonas.stream()
                .filter(z -> z.getTipoZona() == tipoZona && z.isActive())
                .count() + 1L;
        long secuencialGlobal = todasLasZonas.stream()
                .filter(Zona::isActive)
                .count() + 1L;
                
        LocalDateTime ahora = LocalDateTime.now();
        String fecha = ahora.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String hora = ahora.format(DateTimeFormatter.ofPattern("HHmmss"));
        return String.format("TICK-%s%s%d-%d-%s-%s", inicialNombre, inicialTipo, secuencialTipo, secuencialGlobal, fecha, hora);
    }
}
