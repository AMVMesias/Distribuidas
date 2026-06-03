package ec.edu.espe.zonas.dtos;

import java.util.UUID;

import ec.edu.espe.zonas.entidades.EstadoEspacio;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import ec.edu.espe.zonas.entidades.TipoEspacio;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EspacioRequestDto {

    @NotBlank(message = "El codigo es obligatorio")
    private String codigo;

    @NotNull(message = "El ID de la zona es obligatorio")
    private UUID idZona;

    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;

    @NotNull(message = "El tipo es obligatorio")
    @Enumerated(EnumType.STRING)
    private TipoEspacio tipo;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(1)
    @Max(300)
    private Integer capacidad;

    @Enumerated(EnumType.STRING)
    private EstadoEspacio estado;


}
