package ConsultorioMedico.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ConsultorioMedico.model.Insumo;
import ConsultorioMedico.model.TipoInsumo;

public interface InsumoRepository extends JpaRepository<Insumo, Long> {

    boolean existsByCodigo(String codigo);

    List<Insumo> findByTipo(TipoInsumo tipo);

    List<Insumo> findByFechaVencimientoBefore(LocalDate fecha);

    List<Insumo> findAllByOrderByNombreAsc();
}
