package ConsultorioMedico.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import ConsultorioMedico.model.Paciente;

@Repository
public class PacienteRepositoryImpl implements PacienteRepository {
    private final List<Paciente> pacientes = new ArrayList<>();

    public PacienteRepositoryImpl() {
        Paciente p = new Paciente();
        p.setId(1L);
        p.setNombre("Juan Perez");
        p.setDni("12345678");
        pacientes.add(p);
    }

    @Override
    public boolean existsById(Long id) {
        return pacientes.stream().anyMatch(p -> p.getId().equals(id));
    }

    @Override
    public Optional<Paciente> findById(Long id) {
        return pacientes.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    @Override
    public Paciente save(Paciente paciente) {
        paciente.setId((long) (pacientes.size() + 1));
        pacientes.add(paciente);
        return paciente;
    }
}
