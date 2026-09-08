package ConsultorioMedico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ConsultorioMedico.model.Insumo;
import ConsultorioMedico.model.TipoInsumo;
import ConsultorioMedico.repository.ComponenteFormulaRepository;
import ConsultorioMedico.repository.InsumoRepository;
import ConsultorioMedico.service.InsumoService;

@ExtendWith(MockitoExtension.class)
class InsumoServiceTest {

    @Mock
    private InsumoRepository insumoRepository;

    @Mock
    private ComponenteFormulaRepository componenteRepository;

    @InjectMocks
    private InsumoService insumoService;

    private Insumo insumoPrueba;

    @BeforeEach
    void setUp() {
        insumoPrueba = new Insumo();
        insumoPrueba.setId(40L);
        insumoPrueba.setCodigo("INS-001");
        insumoPrueba.setNombre("Hidrocortisona");
        insumoPrueba.setTipo(TipoInsumo.PRINCIPIO_ACTIVO);
        insumoPrueba.setUnidadMedida("g");
        insumoPrueba.setStock(new BigDecimal("100.000"));
        insumoPrueba.setStockMinimo(new BigDecimal("10.000"));
        insumoPrueba.setPrecioUnitario(new BigDecimal("2.50"));
        insumoPrueba.setLote("L-2026-01");
        insumoPrueba.setFechaVencimiento(LocalDate.now().plusMonths(6));
    }

    @Test
    void registrarInsumo_DebeLanzarExcepcion_CuandoElCodigoYaExiste() {
        when(insumoRepository.existsByCodigo("INS-001")).thenReturn(true);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            insumoService.registrarInsumo(insumoPrueba);
        });

        assertTrue(excepcion.getMessage().contains("Ya existe un insumo con el codigo INS-001"));
        verify(insumoRepository, never()).save(any(Insumo.class));
    }

    @Test
    void registrarInsumo_DebeLanzarExcepcion_CuandoYaEstaVencido() {
        insumoPrueba.setFechaVencimiento(LocalDate.now().minusDays(1));
        when(insumoRepository.existsByCodigo("INS-001")).thenReturn(false);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            insumoService.registrarInsumo(insumoPrueba);
        });

        assertTrue(excepcion.getMessage().contains("No se puede registrar un insumo ya vencido"));
        verify(insumoRepository, never()).save(any(Insumo.class));
    }

    @Test
    void registrarInsumo_DebeGuardar_CuandoTodoEsCorrecto() {
        when(insumoRepository.existsByCodigo("INS-001")).thenReturn(false);
        when(insumoRepository.save(any(Insumo.class))).thenReturn(insumoPrueba);

        Insumo resultado = insumoService.registrarInsumo(insumoPrueba);

        assertEquals("Hidrocortisona", resultado.getNombre());
        verify(insumoRepository, times(1)).save(insumoPrueba);
    }

    @Test
    void eliminarInsumo_DebeLanzarExcepcion_CuandoFormaParteDeUnaFormula() {
        when(insumoRepository.existsById(40L)).thenReturn(true);
        when(componenteRepository.existsByInsumoId(40L)).thenReturn(true);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            insumoService.eliminarInsumo(40L);
        });

        assertTrue(excepcion.getMessage().contains("forma parte de una formula"));
        verify(insumoRepository, never()).deleteById(anyLong());
    }

    @Test
    void listarBajoStock_DebeDevolverSoloLosQueEstanDebajoDelMinimo() {
        Insumo escaso = new Insumo();
        escaso.setId(41L);
        escaso.setNombre("Vaselina");
        escaso.setStock(new BigDecimal("5.000"));
        escaso.setStockMinimo(new BigDecimal("20.000"));
        when(insumoRepository.findAll()).thenReturn(List.of(insumoPrueba, escaso));

        List<Insumo> resultado = insumoService.listarBajoStock();

        assertEquals(1, resultado.size());
        assertEquals("Vaselina", resultado.get(0).getNombre());
    }
}