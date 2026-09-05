package ConsultorioMedico.service;

import ConsultorioMedico.model.CitaMedica;
import ConsultorioMedico.repository.CitaMedicaRepository;
import ConsultorioMedico.repository.MedicoRepository;
import ConsultorioMedico.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CitaMedicaService {

    private final CitaMedicaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    public CitaMedicaService(CitaMedicaRepository citaRepository,
            PacienteRepository pacienteRepository,
            MedicoRepository medicoRepository) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
    }

    public CitaMedica registrarCita(CitaMedica cita) {
        if (!pacienteRepository.existsById(cita.getPacienteId())) {
            throw new IllegalArgumentException("Error: El paciente con ID " + cita.getPacienteId() + " no existe.");
        }
        if (!medicoRepository.existsById(cita.getMedicoId())) {
            throw new IllegalArgumentException("Error: El médico con ID " + cita.getMedicoId() + " no existe.");
        }
        if (citaRepository.existsByMedicoIdAndFechaHora(cita.getMedicoId(), cita.getFechaHora())) {
            throw new IllegalArgumentException("Error: El médico ya tiene una cita programada en ese horario exacto.");
        }

        return citaRepository.save(cita);
    }

    public List<CitaMedica> listarCitas() {
        return citaRepository.findAll();
    }

    public Optional<CitaMedica> buscarPorId(Long id) {
        return citaRepository.findById(id);
    }
}