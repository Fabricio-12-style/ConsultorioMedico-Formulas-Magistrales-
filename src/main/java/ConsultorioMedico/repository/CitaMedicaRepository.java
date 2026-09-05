package ConsultorioMedico.repository;

import ConsultorioMedico.model.CitaMedica;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CitaMedicaRepository {
    CitaMedica save(CitaMedica cita);

    Optional<CitaMedica> findById(Long id);

    List<CitaMedica> findAll();

    boolean existsByMedicoIdAndFechaHora(Long medicoId, LocalDateTime fechaHora);
}