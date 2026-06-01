package ec.edu.espe.zonas.servicio.imp;

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
import ec.edu.espe.zonas.repositories.ZonaRepositorio;
import ec.edu.espe.zonas.servicio.ZonaServicio;

import java.time.format.DateTimeFormatter;

@Service
public class ZonaServicioImpl implements ZonaServicio {
    @Autowired
    private ZonaRepositorio zonaRepositorio;

    @Override
    public List<ZonaResponseDto> listarZonas() {
        return zonaRepositorio.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ZonaResponseDto crearZona(ZonaRequestDto zona) {
        if (zonaRepositorio.existsByNombre(zona.getNombre())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una zona con el mismo nombre");
        }

        Zona objZona = new Zona();

        objZona.setNombre(zona.getNombre());
        objZona.setDescripcion(zona.getDescripcion());
        
        // Pass the TipoZona to the code generator to make it dynamic
        objZona.setCodigo(generarCodigoZona(zona.getTipo().name())); 
        
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

        if (!objZona.getNombre().equals(zona.getNombre()) && zonaRepositorio.existsByNombre(zona.getNombre())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una zona con el mismo nombre");
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

    private String generarCodigoZona(String prefijoTipo) {
        // Formato esperado: TICK-[TIPO]-23-YYYYMMDD-HHMMSS
        // Ejemplo: TICK-VIP-23-20260520-104237
        
        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        String fecha = ahora.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String hora = ahora.format(DateTimeFormatter.ofPattern("HHmmss"));
        
        // Acortamos el tipo a 3 letras si es muy largo para que no quede gigante (ej. VIP, REG, EXT)
        String prefijoCorto = prefijoTipo.length() >= 3 ? prefijoTipo.substring(0, 3).toUpperCase() : prefijoTipo.toUpperCase();
        
        return String.format("TICK-%s-23-%s-%s", prefijoCorto, fecha, hora);
    }
}
