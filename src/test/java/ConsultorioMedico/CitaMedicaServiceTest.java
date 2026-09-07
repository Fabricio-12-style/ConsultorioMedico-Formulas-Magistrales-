package ConsultorioMedico;

import ConsultorioMedico.model.CitaMedica;
import ConsultorioMedico.model.EstadoCita;
import ConsultorioMedico.repository.CitaMedicaRepository;
import ConsultorioMedico.repository.MedicoRepository;
import ConsultorioMedico.repository.PacienteRepository;
import ConsultorioMedico.service.CitaMedicaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitaMedicaServiceTest {

    @Mock
    private CitaMedicaRepository citaRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private MedicoRepository medicoRepository;

    @InjectMocks
    private CitaMedicaService citaService;

    private CitaMedica citaPrueba;

    @BeforeEach
    void setUp() {
        citaPrueba = new CitaMedica();
        citaPrueba.setId(1L);
        citaPrueba.setPacienteId(10L);
        citaPrueba.setMedicoId(20L);
        citaPrueba.setFechaHora(LocalDateTime.of(2026, 9, 10, 10, 0));
        citaPrueba.setMotivo("Consulta General");
        citaPrueba.setEstado(EstadoCita.PENDIENTE);
    }

    @Test
    void registrarCita_DebeLanzarExcepcion_CuandoPacienteNoExiste() {
        when(pacienteRepository.existsById(10L)).thenReturn(false);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            citaService.registrarCita(citaPrueba);
        });

        assertTrue(excepcion.getMessage().contains("El paciente con ID 10 no existe"));

        verify(citaRepository, never()).save(any(CitaMedica.class));
    }

    @Test
    void registrarCita_DebeLanzarExcepcion_CuandoMedicoNoExiste() {
        // Arrange
        when(pacienteRepository.existsById(10L)).thenReturn(true);
        when(medicoRepository.existsById(20L)).thenReturn(false);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            citaService.registrarCita(citaPrueba);
        });

        assertTrue(excepcion.getMessage().contains("El médico con ID 20 no existe"));
        verify(citaRepository, never()).save(any(CitaMedica.class));
    }

    @Test
    void registrarCita_DebeLanzarExcepcion_CuandoHayCruceDeHorarios() {
        when(pacienteRepository.existsById(10L)).thenReturn(true);
        when(medicoRepository.existsById(20L)).thenReturn(true);
        when(citaRepository.existsByMedicoIdAndFechaHora(20L, citaPrueba.getFechaHora())).thenReturn(true);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            citaService.registrarCita(citaPrueba);
        });

        assertTrue(excepcion.getMessage().contains("El médico ya tiene una cita programada"));
        verify(citaRepository, never()).save(any(CitaMedica.class));
    }

    @Test
    void registrarCita_DebeGuardarCita_CuandoDatosSonCorrectos() {
        // Arrange: Todo está correcto en la BD simulada
        when(pacienteRepository.existsById(10L)).thenReturn(true);
        when(medicoRepository.existsById(20L)).thenReturn(true);
        when(citaRepository.existsByMedicoIdAndFechaHora(20L, citaPrueba.getFechaHora())).thenReturn(false);
        when(citaRepository.save(any(CitaMedica.class))).thenReturn(citaPrueba);

        CitaMedica resultado = citaService.registrarCita(citaPrueba);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getPacienteId());
        verify(citaRepository, times(1)).save(citaPrueba);
    }

    @Test
    void registrarCita_DebeLanzarExcepcion_CuandoEsDomingo() {
        citaPrueba.setFechaHora(LocalDateTime.of(2026, 9, 13, 10, 0));
        when(pacienteRepository.existsById(10L)).thenReturn(true);
        when(medicoRepository.existsById(20L)).thenReturn(true);
        when(citaRepository.existsByMedicoIdAndFechaHora(20L, citaPrueba.getFechaHora())).thenReturn(false);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            citaService.registrarCita(citaPrueba);
        });

        assertTrue(excepcion.getMessage().contains("no atiende los domingos"));
        verify(citaRepository, never()).save(any(CitaMedica.class));
    }

    @Test
    void registrarCita_DebeLanzarExcepcion_CuandoEstaFueraDelHorarioLaboral() {
        citaPrueba.setFechaHora(LocalDateTime.of(2026, 9, 10, 22, 0));
        when(pacienteRepository.existsById(10L)).thenReturn(true);
        when(medicoRepository.existsById(20L)).thenReturn(true);
        when(citaRepository.existsByMedicoIdAndFechaHora(20L, citaPrueba.getFechaHora())).thenReturn(false);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            citaService.registrarCita(citaPrueba);
        });

        assertTrue(excepcion.getMessage().contains("fuera del horario laboral"));
        verify(citaRepository, never()).save(any(CitaMedica.class));
    }

    @Test
    void actualizarCita_DebeDevolverVacio_CuandoLaCitaNoExiste() {
        when(citaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<CitaMedica> resultado = citaService.actualizarCita(99L, citaPrueba);

        assertTrue(resultado.isEmpty());
        verify(citaRepository, never()).save(any(CitaMedica.class));
    }

    @Test
    void eliminarCita_DebeDevolverFalse_CuandoLaCitaNoExiste() {
        when(citaRepository.existsById(99L)).thenReturn(false);

        assertFalse(citaService.eliminarCita(99L));

        verify(citaRepository, never()).deleteById(anyLong());
    }
}