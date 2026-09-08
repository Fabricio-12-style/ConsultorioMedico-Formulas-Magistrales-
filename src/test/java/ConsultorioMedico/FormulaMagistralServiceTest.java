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
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ConsultorioMedico.model.ComponenteFormula;
import ConsultorioMedico.model.FormaFarmaceutica;
import ConsultorioMedico.model.FormulaMagistral;
import ConsultorioMedico.model.Insumo;
import ConsultorioMedico.model.TipoInsumo;
import ConsultorioMedico.repository.ComponenteFormulaRepository;
import ConsultorioMedico.repository.FormulaMagistralRepository;
import ConsultorioMedico.repository.InsumoRepository;
import ConsultorioMedico.repository.PreparacionRepository;
import ConsultorioMedico.service.FormulaMagistralService;

@ExtendWith(MockitoExtension.class)
class FormulaMagistralServiceTest {

    @Mock
    private FormulaMagistralRepository formulaRepository;

    @Mock
    private ComponenteFormulaRepository componenteRepository;

    @Mock
    private InsumoRepository insumoRepository;

    @Mock
    private PreparacionRepository preparacionRepository;

    @InjectMocks
    private FormulaMagistralService formulaService;

    private FormulaMagistral formulaPrueba;
    private Insumo insumoPrueba;
    private ComponenteFormula componentePrueba;

    @BeforeEach
    void setUp() {
        formulaPrueba = new FormulaMagistral();
        formulaPrueba.setId(30L);
        formulaPrueba.setCodigo("FM-001");
        formulaPrueba.setNombre("Crema de hidrocortisona al 2%");
        formulaPrueba.setFormaFarmaceutica(FormaFarmaceutica.CREMA);
        formulaPrueba.setViaAdministracion("TOPICA");
        formulaPrueba.setActivo(true);

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

        componentePrueba = new ComponenteFormula();
        componentePrueba.setInsumoId(40L);
        componentePrueba.setCantidad(new BigDecimal("5.000"));
        componentePrueba.setUnidadMedida("g");
    }

    @Test
    void registrarFormula_DebeLanzarExcepcion_CuandoElCodigoYaExiste() {
        when(formulaRepository.existsByCodigo("FM-001")).thenReturn(true);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            formulaService.registrarFormula(formulaPrueba);
        });

        assertTrue(excepcion.getMessage().contains("Ya existe una formula con el codigo FM-001"));
        verify(formulaRepository, never()).save(any(FormulaMagistral.class));
    }

    @Test
    void registrarFormula_DebeGuardar_CuandoElCodigoEsNuevo() {
        when(formulaRepository.existsByCodigo("FM-001")).thenReturn(false);
        when(formulaRepository.save(any(FormulaMagistral.class))).thenReturn(formulaPrueba);

        FormulaMagistral resultado = formulaService.registrarFormula(formulaPrueba);

        assertEquals("FM-001", resultado.getCodigo());
        verify(formulaRepository, times(1)).save(formulaPrueba);
    }

    @Test
    void eliminarFormula_DebeLanzarExcepcion_CuandoTienePreparaciones() {
        when(formulaRepository.existsById(30L)).thenReturn(true);
        when(preparacionRepository.existsByFormulaId(30L)).thenReturn(true);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            formulaService.eliminarFormula(30L);
        });

        assertTrue(excepcion.getMessage().contains("tiene preparaciones registradas"));
        verify(formulaRepository, never()).deleteById(anyLong());
    }

    @Test
    void agregarInsumo_DebeLanzarExcepcion_CuandoElInsumoYaEstaEnLaFormula() {
        when(formulaRepository.existsById(30L)).thenReturn(true);
        when(insumoRepository.findById(40L)).thenReturn(Optional.of(insumoPrueba));
        when(componenteRepository.existsByFormulaIdAndInsumoId(30L, 40L)).thenReturn(true);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            formulaService.agregarInsumo(30L, componentePrueba);
        });

        assertTrue(excepcion.getMessage().contains("ya forma parte de la formula"));
        verify(componenteRepository, never()).save(any(ComponenteFormula.class));
    }

    @Test
    void agregarInsumo_DebeLanzarExcepcion_CuandoLaUnidadNoCoincide() {
        componentePrueba.setUnidadMedida("ml");
        when(formulaRepository.existsById(30L)).thenReturn(true);
        when(insumoRepository.findById(40L)).thenReturn(Optional.of(insumoPrueba));
        when(componenteRepository.existsByFormulaIdAndInsumoId(30L, 40L)).thenReturn(false);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            formulaService.agregarInsumo(30L, componentePrueba);
        });

        assertTrue(excepcion.getMessage().contains("La unidad de medida debe ser g"));
        verify(componenteRepository, never()).save(any(ComponenteFormula.class));
    }

    @Test
    void agregarInsumo_DebeGuardar_CuandoTodoEsCorrecto() {
        when(formulaRepository.existsById(30L)).thenReturn(true);
        when(insumoRepository.findById(40L)).thenReturn(Optional.of(insumoPrueba));
        when(componenteRepository.existsByFormulaIdAndInsumoId(30L, 40L)).thenReturn(false);
        when(componenteRepository.save(any(ComponenteFormula.class))).thenReturn(componentePrueba);

        ComponenteFormula resultado = formulaService.agregarInsumo(30L, componentePrueba);

        assertEquals(30L, resultado.getFormulaId());
        verify(componenteRepository, times(1)).save(componentePrueba);
    }
}