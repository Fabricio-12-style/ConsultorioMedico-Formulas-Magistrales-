package ConsultorioMedico.service;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ConsultorioMedico.model.Paciente;
import ConsultorioMedico.repository.CitaMedicaRepository;
import ConsultorioMedico.repository.PacienteRepository;

@Service
public class PacienteService {
    
    private final PacienteRepository pacienteRepository;
    private final CitaMedicaRepository citaRepository;

    public PacienteService(PacienteRepository pacienteRepository, CitaMedicaRepository citaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.citaRepository = citaRepository;
    }

    public Paciente registrarPaciente(Paciente paciente) {
        if (pacienteRepository.existsByDni(paciente.getDni())) {
            throw new IllegalArgumentException(
                    "Error: Ya existe un paciente registrado con el DNI " + paciente.getDni() + ".");
        }
        return pacienteRepository.save(paciente);
    }

    public Optional<Paciente> actualizarPaciente(Long id, Paciente datos) {
        return pacienteRepository.findById(id).map(paciente -> {
            if (!paciente.getDni().equals(datos.getDni()) && pacienteRepository.existsByDni(datos.getDni())) {
                throw new IllegalArgumentException(
                        "Error: Ya existe un paciente registrado con el DNI " + datos.getDni() + ".");
            }
            paciente.setNombre(datos.getNombre());
            paciente.setDni(datos.getDni());
            paciente.setTelefono(datos.getTelefono());
            paciente.setEmail(datos.getEmail());
            return pacienteRepository.save(paciente);
        });
    }

    public boolean eliminarPaciente(Long id) {
        if (!pacienteRepository.existsById(id)) {
            return false;
        }
        if (citaRepository.existsByPacienteId(id)) {
            throw new IllegalArgumentException(
                    "Error: No se puede eliminar el paciente porque tiene citas registradas.");
        }
        pacienteRepository.deleteById(id);
        return true;
    }

    public List<Paciente> listarPacientes() {
        return pacienteRepository.findAll();
    }

    public Optional<Paciente> buscarPorId(Long id) {
        return pacienteRepository.findById(id);
    }
}
