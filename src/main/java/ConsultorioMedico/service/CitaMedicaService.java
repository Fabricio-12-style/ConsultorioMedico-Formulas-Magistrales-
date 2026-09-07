package ConsultorioMedico.service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ConsultorioMedico.model.CitaMedica;
import ConsultorioMedico.repository.CitaMedicaRepository;
import ConsultorioMedico.repository.MedicoRepository;
import ConsultorioMedico.repository.PacienteRepository;

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
        validarHorarioDeAtencion(cita);

        return citaRepository.save(cita);
    }

    public Optional<CitaMedica> actualizarCita(Long id, CitaMedica datos) {
        return citaRepository.findById(id).map(cita -> {
            if (!pacienteRepository.existsById(datos.getPacienteId())) {
                throw new IllegalArgumentException("Error: El paciente con ID " + datos.getPacienteId() + " no existe.");
            }
            if (!medicoRepository.existsById(datos.getMedicoId())) {
                throw new IllegalArgumentException("Error: El médico con ID " + datos.getMedicoId() + " no existe.");
            }
            validarHorarioDeAtencion(datos);

            cita.setPacienteId(datos.getPacienteId());
            cita.setMedicoId(datos.getMedicoId());
            cita.setFechaHora(datos.getFechaHora());
            cita.setMotivo(datos.getMotivo());
            if (datos.getEstado() != null) {
                cita.setEstado(datos.getEstado());
            }
            return citaRepository.save(cita);
        });
    }

    public boolean eliminarCita(Long id) {
        if (!citaRepository.existsById(id)) {
            return false;
        }
        citaRepository.deleteById(id);
        return true;
    }

    public List<CitaMedica> listarCitas() {
        return citaRepository.findAllByOrderByFechaHoraAsc();
    }

    public Optional<CitaMedica> buscarPorId(Long id) {
        return citaRepository.findById(id);
    }

    private void validarHorarioDeAtencion(CitaMedica cita) {
        if (cita.getFechaHora().getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("Error: El consultorio no atiende los domingos.");
        }
        int hora = cita.getFechaHora().getHour();
        if (hora < 8 || hora > 18) {
            throw new IllegalArgumentException("Error: Cita fuera del horario laboral (08:00 - 18:00).");
        }
    }
}
