package ec.edu.espe.zonas.servicio.imp;

import ec.edu.espe.zonas.dtos.EspacioRequestDto;
import ec.edu.espe.zonas.dtos.EspaciosResponseDto;
import ec.edu.espe.zonas.entidades.Espacio;
import ec.edu.espe.zonas.entidades.EstadoEspacio;
import ec.edu.espe.zonas.entidades.Zona;
import ec.edu.espe.zonas.repositories.EspacioRepositorio;
import ec.edu.espe.zonas.repositories.ZonaRepositorio;
import ec.edu.espe.zonas.servicio.EspacioServicio;
import ec.edu.espe.zonas.utils.UtilsMappers;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EspacioServicioImp implements EspacioServicio {

    private final EspacioRepositorio repositorioEspacio;
    private final ZonaRepositorio zonaRepositorio;
    private final UtilsMappers mapper;

    @Override
    @Transactional(readOnly = true)
    public List<EspaciosResponseDto> obtenerEspacios() {
        return repositorioEspacio.findAll().stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public EspaciosResponseDto crearEspacio(EspacioRequestDto dto) {
        Zona objZona = zonaRepositorio.findById(dto.getIdZona())
                .orElseThrow(() -> new RuntimeException("Zona no encontrada con id: " + dto.getIdZona()));

        Espacio nuevoEspacio = mapper.toEntityEspacio(dto);
        nuevoEspacio.setZona(objZona);
        nuevoEspacio.setEstado(EstadoEspacio.DISPONIBLE); // Por defecto, el nuevo espacio estará disponible

        Espacio espacioSaved = repositorioEspacio.save(nuevoEspacio);

        return mapper.toResponseDto(espacioSaved);
    }

    @Override
    public EspaciosResponseDto actualizarEspacio(EspacioRequestDto dto, UUID idEspacio) {
        return null;
    }

    @Override
    public void eliminiarEspacio(UUID idEspacio) {

    }

    @Override
    @Transactional
    public EspaciosResponseDto cambiarEstado(UUID idEspacio, EstadoEspacio estado) {
        Espacio espacio = repositorioEspacio.findById(idEspacio)
                .orElseThrow(() -> new RuntimeException("Espacio no encontrado con id: " + idEspacio));
        espacio.setEstado(estado);
        Espacio espacioSaved = repositorioEspacio.save(espacio);
        return mapper.toResponseDto(espacioSaved);
    }

    @Override
    public List<EspaciosResponseDto> obtenerEspacioPorEstado(EstadoEspacio estado) {
        return List.of();
    }

    @Override
    public List<EspaciosResponseDto> obtenerEspacioPorZonaEsgtado(EstadoEspacio estado) {
        return List.of();
    }
}
