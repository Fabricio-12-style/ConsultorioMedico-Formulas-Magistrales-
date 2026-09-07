package ConsultorioMedico.service;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ConsultorioMedico.model.Medico;
import ConsultorioMedico.repository.CitaMedicaRepository;
import ConsultorioMedico.repository.MedicoRepository;

@Service
public class MedicoService {
    
    private final MedicoRepository medicoRepository;
    private final CitaMedicaRepository citaRepository;

    public MedicoService(MedicoRepository medicoRepository, CitaMedicaRepository citaRepository) {
        this.medicoRepository = medicoRepository;
        this.citaRepository = citaRepository;
    }

    public Medico registrarMedico(Medico medico) {
        if (medicoRepository.existsByCmp(medico.getCmp())) {
            throw new IllegalArgumentException(
                    "Error: Ya existe un medico registrado con el CMP " + medico.getCmp() + ".");
        }
        return medicoRepository.save(medico);
    }

    public Optional<Medico> actualizarMedico(Long id, Medico datos) {
        return medicoRepository.findById(id).map(medico -> {
            if (!medico.getCmp().equals(datos.getCmp()) && medicoRepository.existsByCmp(datos.getCmp())) {
                throw new IllegalArgumentException(
                        "Error: Ya existe un medico registrado con el CMP " + datos.getCmp() + ".");
            }
            medico.setNombre(datos.getNombre());
            medico.setCmp(datos.getCmp());
            medico.setEspecialidad(datos.getEspecialidad());
            return medicoRepository.save(medico);
        });
    }

    public boolean eliminarMedico(Long id) {
        if (!medicoRepository.existsById(id)) {
            return false;
        }
        if (citaRepository.existsByMedicoId(id)) {
            throw new IllegalArgumentException(
                    "Error: No se puede eliminar el medico porque tiene citas registradas.");
        }
        medicoRepository.deleteById(id);
        return true;
    }

    public List<Medico> listarMedicos() {
        return medicoRepository.findAll();
    }

    public Optional<Medico> buscarPorId(Long id) {
        return medicoRepository.findById(id);
    }
}

