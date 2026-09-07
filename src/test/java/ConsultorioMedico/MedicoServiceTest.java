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

import ConsultorioMedico.model.Medico;
import ConsultorioMedico.repository.CitaMedicaRepository;
import ConsultorioMedico.repository.MedicoRepository;
import ConsultorioMedico.service.MedicoService;

@ExtendWith(MockitoExtension.class)
public class MedicoServiceTest {
        @Mock
    private MedicoRepository medicoRepository;

    @Mock
    private CitaMedicaRepository citaRepository;

    @InjectMocks
    private MedicoService medicoService;

    private Medico medicoPrueba;

    @BeforeEach
    void setUp() {
        medicoPrueba = new Medico();
        medicoPrueba.setId(1L);
        medicoPrueba.setNombre("Dra. Ana Gomez");
        medicoPrueba.setCmp("CMP-10234");
        medicoPrueba.setEspecialidad("Cardiologia");
    }

    @Test
    void registrarMedico_DebeLanzarExcepcion_CuandoElCmpYaExiste() {
        when(medicoRepository.existsByCmp("CMP-10234")).thenReturn(true);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            medicoService.registrarMedico(medicoPrueba);
        });

        assertTrue(excepcion.getMessage().contains("Ya existe un medico registrado con el CMP CMP-10234"));
        verify(medicoRepository, never()).save(any(Medico.class));
    }

    @Test
    void registrarMedico_DebeGuardar_CuandoElCmpEsNuevo() {
        when(medicoRepository.existsByCmp("CMP-10234")).thenReturn(false);
        when(medicoRepository.save(any(Medico.class))).thenReturn(medicoPrueba);

        Medico resultado = medicoService.registrarMedico(medicoPrueba);

        assertEquals("Cardiologia", resultado.getEspecialidad());
        verify(medicoRepository, times(1)).save(medicoPrueba);
    }

    @Test
    void eliminarMedico_DebeLanzarExcepcion_CuandoTieneCitasRegistradas() {
        when(medicoRepository.existsById(1L)).thenReturn(true);
        when(citaRepository.existsByMedicoId(1L)).thenReturn(true);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            medicoService.eliminarMedico(1L);
        });

        assertTrue(excepcion.getMessage().contains("tiene citas registradas"));
        verify(medicoRepository, never()).deleteById(anyLong());
    }

    @Test
    void eliminarMedico_DebeDevolverFalse_CuandoNoExiste() {
        when(medicoRepository.existsById(99L)).thenReturn(false);

        assertFalse(medicoService.eliminarMedico(99L));

        verify(medicoRepository, never()).deleteById(anyLong());
    }
}
