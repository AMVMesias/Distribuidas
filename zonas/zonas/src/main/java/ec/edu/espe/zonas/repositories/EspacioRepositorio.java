package ec.edu.espe.zonas.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ec.edu.espe.zonas.models.Espacio;

public interface EspacioRepositorio extends JpaRepository<Espacio, UUID> {
    boolean existsByCodigo(String codigo);

    List<Espacio> findByZona(UUID zonaId);

    List<Espacio> findByTipoAndEstado(ec.edu.espe.zonas.models.TipoEspacio tipo, boolean estado);
}
