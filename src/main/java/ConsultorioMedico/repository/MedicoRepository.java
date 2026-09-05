package ConsultorioMedico.repository;
import java.util.Optional;
import ConsultorioMedico.model.Medico;

public interface MedicoRepository {
    boolean existsById(Long id);
    Optional<Medico> findById(Long id);
}
