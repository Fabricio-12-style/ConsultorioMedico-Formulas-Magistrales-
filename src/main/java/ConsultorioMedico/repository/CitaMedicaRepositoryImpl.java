package ConsultorioMedico.repository;

import ConsultorioMedico.model.CitaMedica;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CitaMedicaRepositoryImpl implements CitaMedicaRepository {
    private final List<CitaMedica> citas = new ArrayList<>();
    private Long generadorId = 1L;

    @Override
    public CitaMedica save(CitaMedica cita) {
        if (cita.getId() == null) {
            cita.setId(generadorId++);
            citas.add(cita);
        } else {
            citas.removeIf(c -> c.getId().equals(cita.getId()));
            citas.add(cita);
        }
        return cita;
    }

    @Override
    public Optional<CitaMedica> findById(Long id) {
        return citas.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    @Override
    public List<CitaMedica> findAll() {
        return new ArrayList<>(citas);
    }

    @Override
    public boolean existsByMedicoIdAndFechaHora(Long medicoId, LocalDateTime fechaHora) {
        return citas.stream()
                .anyMatch(c -> c.getMedicoId().equals(medicoId) && c.getFechaHora().equals(fechaHora));
    }
}