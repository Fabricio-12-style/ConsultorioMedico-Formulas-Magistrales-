package ConsultorioMedico.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ConsultorioMedico.model.CitaMedica;

public interface CitaMedicaRepository extends JpaRepository<CitaMedica, Long> {

    boolean existsByMedicoIdAndFechaHora(Long medicoId, LocalDateTime fechaHora);

    boolean existsByPacienteId(Long pacienteId);

    boolean existsByMedicoId(Long medicoId);

    List<CitaMedica> findAllByOrderByFechaHoraAsc();
}
