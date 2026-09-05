package ConsultorioMedico.repository;
import ConsultorioMedico.model.Paciente;
import java.util.Optional;

public interface PacienteRepository {
    boolean existsById(Long id);
    Optional<Paciente> findById(Long id);
    Paciente save(Paciente paciente);
}
