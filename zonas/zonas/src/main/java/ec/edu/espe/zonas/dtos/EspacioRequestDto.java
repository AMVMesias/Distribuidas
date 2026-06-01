package ec.edu.espe.zonas.dtos;

import java.util.UUID;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import ec.edu.espe.zonas.models.TipoEspacio;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EspacioRequestDto {

    @NotNull(message = "El ID de la zona es obligatorio")
    @NotBlank(message = "El ID de la zona no puede estar vacio")
    private UUID idZona;

    @Enumerated(EnumType.STRING)
    @NotBlank(message = "El estado es obligatorio")
    private String descripcion;

    private TipoEspacio tipo;
}
