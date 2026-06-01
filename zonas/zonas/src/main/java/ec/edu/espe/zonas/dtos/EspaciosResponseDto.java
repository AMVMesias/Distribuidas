package ec.edu.espe.zonas.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import ec.edu.espe.zonas.models.TipoEspacio;
import ec.edu.espe.zonas.models.Zona;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EspaciosResponseDto {
    private UUID id;

    private String codigo;

    private Zona zona;

    private String descripcion;

    private TipoEspacio tipo;

    private boolean estado;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;
}
