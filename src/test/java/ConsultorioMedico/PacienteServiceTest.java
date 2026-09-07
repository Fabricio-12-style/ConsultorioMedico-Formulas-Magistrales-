package ConsultorioMedico;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ConsultorioMedico.model.Paciente;
import ConsultorioMedico.repository.CitaMedicaRepository;
import ConsultorioMedico.repository.PacienteRepository;
import ConsultorioMedico.service.PacienteService;

@ExtendWith(MockitoExtension.class)
public class PacienteServiceTest {
        @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private CitaMedicaRepository citaRepository;

    @InjectMocks
    private PacienteService pacienteService;

    private Paciente pacientePrueba;

    @BeforeEach
    void setUp() {
        pacientePrueba = new Paciente();
        pacientePrueba.setId(1L);
        pacientePrueba.setNombre("Juan Perez");
        pacientePrueba.setDni("12345678");
        pacientePrueba.setTelefono("987654321");
        pacientePrueba.setEmail("juan.perez@example.com");
    }

    @Test
    void registrarPaciente_DebeLanzarExcepcion_CuandoElDniYaExiste() {
        when(pacienteRepository.existsByDni("12345678")).thenReturn(true);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            pacienteService.registrarPaciente(pacientePrueba);
        });

        assertTrue(excepcion.getMessage().contains("Ya existe un paciente registrado con el DNI 12345678"));
        verify(pacienteRepository, never()).save(any(Paciente.class));
    }

    @Test
    void registrarPaciente_DebeGuardar_CuandoElDniEsNuevo() {
        when(pacienteRepository.existsByDni("12345678")).thenReturn(false);
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacientePrueba);

        Paciente resultado = pacienteService.registrarPaciente(pacientePrueba);

        assertEquals("Juan Perez", resultado.getNombre());
        verify(pacienteRepository, times(1)).save(pacientePrueba);
    }

    @Test
    void eliminarPaciente_DebeLanzarExcepcion_CuandoTieneCitasRegistradas() {
        when(pacienteRepository.existsById(1L)).thenReturn(true);
        when(citaRepository.existsByPacienteId(1L)).thenReturn(true);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            pacienteService.eliminarPaciente(1L);
        });

        assertTrue(excepcion.getMessage().contains("tiene citas registradas"));
        verify(pacienteRepository, never()).deleteById(anyLong());
    }

    @Test
    void eliminarPaciente_DebeDevolverFalse_CuandoNoExiste() {
        when(pacienteRepository.existsById(99L)).thenReturn(false);

        assertFalse(pacienteService.eliminarPaciente(99L));

        verify(pacienteRepository, never()).deleteById(anyLong());
    }

    @Test
    void eliminarPaciente_DebeEliminar_CuandoNoTieneCitas() {
        when(pacienteRepository.existsById(1L)).thenReturn(true);
        when(citaRepository.existsByPacienteId(1L)).thenReturn(false);

        assertTrue(pacienteService.eliminarPaciente(1L));

        verify(pacienteRepository, times(1)).deleteById(1L);
    }
}
