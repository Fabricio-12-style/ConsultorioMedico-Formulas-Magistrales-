package ConsultorioMedico.repository;

import ConsultorioMedico.model.Medico;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MedicoRepositoryImpl implements MedicoRepository {
    private final List<Medico> medicos = new ArrayList<>();

    public MedicoRepositoryImpl() {
        Medico m = new Medico();
        m.setId(1L);
        m.setNombre("Dra. Ana Gomez");
        m.setEspecialidad("Cardiología");
        medicos.add(m);
    }

    @Override
    public boolean existsById(Long id) {
        return medicos.stream().anyMatch(m -> m.getId().equals(id));
    }

    @Override
    public Optional<Medico> findById(Long id) {
        return medicos.stream().filter(m -> m.getId().equals(id)).findFirst();
    }
}