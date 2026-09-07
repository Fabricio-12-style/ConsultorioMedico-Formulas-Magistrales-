package ConsultorioMedico.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ConsultorioMedico.model.EstadoPreparacion;
import ConsultorioMedico.model.Preparacion;

public interface PreparacionRepository extends JpaRepository<Preparacion, Long> {

    List<Preparacion> findAllByOrderByFechaSolicitudDesc();

    List<Preparacion> findByEstado(EstadoPreparacion estado);

    List<Preparacion> findByPacienteId(Long pacienteId);

    boolean existsByFormulaId(Long formulaId);
}
