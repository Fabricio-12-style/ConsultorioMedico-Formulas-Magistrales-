package ConsultorioMedico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ConsultorioMedico.model.ComponenteFormula;
import ConsultorioMedico.model.EstadoPreparacion;
import ConsultorioMedico.model.FormaFarmaceutica;
import ConsultorioMedico.model.FormulaMagistral;
import ConsultorioMedico.model.Insumo;
import ConsultorioMedico.model.Preparacion;
import ConsultorioMedico.model.TipoInsumo;
import ConsultorioMedico.repository.ComponenteFormulaRepository;
import ConsultorioMedico.repository.FormulaMagistralRepository;
import ConsultorioMedico.repository.InsumoRepository;
import ConsultorioMedico.repository.MedicoRepository;
import ConsultorioMedico.repository.PacienteRepository;
import ConsultorioMedico.repository.PreparacionRepository;
import ConsultorioMedico.service.PreparacionService;

@ExtendWith(MockitoExtension.class)
class PreparacionServiceTest {

    @Mock
    private PreparacionRepository preparacionRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private MedicoRepository medicoRepository;

    @Mock
    private FormulaMagistralRepository formulaRepository;

    @Mock
    private ComponenteFormulaRepository componenteRepository;

    @Mock
    private InsumoRepository insumoRepository;

    @InjectMocks
    private PreparacionService preparacionService;

    private Preparacion preparacionPrueba;
    private FormulaMagistral formulaPrueba;
    private ComponenteFormula componentePrueba;
    private Insumo insumoPrueba;

    @BeforeEach
    void setUp() {
        preparacionPrueba = new Preparacion();
        preparacionPrueba.setId(1L);
        preparacionPrueba.setPacienteId(10L);
        preparacionPrueba.setMedicoId(20L);
        preparacionPrueba.setFormulaId(30L);
        preparacionPrueba.setCantidad(2);
        preparacionPrueba.setEstado(EstadoPreparacion.PENDIENTE);

        formulaPrueba = new FormulaMagistral();
        formulaPrueba.setId(30L);
        formulaPrueba.setCodigo("FM-001");
        formulaPrueba.setNombre("Crema de hidrocortisona al 2%");
        formulaPrueba.setFormaFarmaceutica(FormaFarmaceutica.CREMA);
        formulaPrueba.setViaAdministracion("TOPICA");
        formulaPrueba.setActivo(true);

        componentePrueba = new ComponenteFormula();
        componentePrueba.setId(1L);
        componentePrueba.setFormulaId(30L);
        componentePrueba.setInsumoId(40L);
        componentePrueba.setCantidad(new BigDecimal("5.000"));
        componentePrueba.setUnidadMedida("g");

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
    void registrarPreparacion_DebeLanzarExcepcion_CuandoPacienteNoExiste() {
        when(pacienteRepository.existsById(10L)).thenReturn(false);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            preparacionService.registrarPreparacion(preparacionPrueba);
        });

        assertTrue(excepcion.getMessage().contains("El paciente con ID 10 no existe"));
        verify(preparacionRepository, never()).save(any(Preparacion.class));
    }

    @Test
    void registrarPreparacion_DebeLanzarExcepcion_CuandoMedicoNoExiste() {
        when(pacienteRepository.existsById(10L)).thenReturn(true);
        when(medicoRepository.existsById(20L)).thenReturn(false);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            preparacionService.registrarPreparacion(preparacionPrueba);
        });

        assertTrue(excepcion.getMessage().contains("El medico con ID 20 no existe"));
        verify(preparacionRepository, never()).save(any(Preparacion.class));
    }

    @Test
    void registrarPreparacion_DebeLanzarExcepcion_CuandoFormulaNoExiste() {
        when(pacienteRepository.existsById(10L)).thenReturn(true);
        when(medicoRepository.existsById(20L)).thenReturn(true);
        when(formulaRepository.findById(30L)).thenReturn(Optional.empty());

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            preparacionService.registrarPreparacion(preparacionPrueba);
        });

        assertTrue(excepcion.getMessage().contains("La formula con ID 30 no existe"));
        verify(preparacionRepository, never()).save(any(Preparacion.class));
    }

    @Test
    void registrarPreparacion_DebeLanzarExcepcion_CuandoNoAlcanzaElStock() {
        insumoPrueba.setStock(new BigDecimal("3.000"));
        prepararEscenarioValido();

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            preparacionService.registrarPreparacion(preparacionPrueba);
        });

        assertTrue(excepcion.getMessage().contains("Stock insuficiente de Hidrocortisona"));
        verify(preparacionRepository, never()).save(any(Preparacion.class));
    }

    @Test
    void registrarPreparacion_DebeLanzarExcepcion_CuandoElInsumoEstaVencido() {
        insumoPrueba.setFechaVencimiento(LocalDate.now().minusDays(1));
        prepararEscenarioValido();

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            preparacionService.registrarPreparacion(preparacionPrueba);
        });

        assertTrue(excepcion.getMessage().contains("esta vencido"));
        verify(preparacionRepository, never()).save(any(Preparacion.class));
    }

    @Test
    void registrarPreparacion_DebeLanzarExcepcion_CuandoLaFormulaNoTieneInsumos() {
        when(pacienteRepository.existsById(10L)).thenReturn(true);
        when(medicoRepository.existsById(20L)).thenReturn(true);
        when(formulaRepository.findById(30L)).thenReturn(Optional.of(formulaPrueba));
        when(componenteRepository.findByFormulaId(30L)).thenReturn(List.of());

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            preparacionService.registrarPreparacion(preparacionPrueba);
        });

        assertTrue(excepcion.getMessage().contains("no tiene insumos registrados"));
        verify(preparacionRepository, never()).save(any(Preparacion.class));
    }

    @Test
    void registrarPreparacion_DebeGuardarComoPendiente_CuandoTodoEsCorrecto() {
        prepararEscenarioValido();
        when(preparacionRepository.save(any(Preparacion.class))).thenReturn(preparacionPrueba);

        Preparacion resultado = preparacionService.registrarPreparacion(preparacionPrueba);

        assertEquals(EstadoPreparacion.PENDIENTE, resultado.getEstado());
        verify(preparacionRepository, times(1)).save(preparacionPrueba);
        verify(insumoRepository, never()).save(any(Insumo.class));
    }

    @Test
    void iniciarPreparacion_DebeDescontarElStock() {
        when(preparacionRepository.findById(1L)).thenReturn(Optional.of(preparacionPrueba));
        when(componenteRepository.findByFormulaId(30L)).thenReturn(List.of(componentePrueba));
        when(insumoRepository.findById(40L)).thenReturn(Optional.of(insumoPrueba));
        when(preparacionRepository.save(any(Preparacion.class))).thenReturn(preparacionPrueba);

        Optional<Preparacion> resultado = preparacionService.iniciarPreparacion(1L);

        assertTrue(resultado.isPresent());
        assertEquals(EstadoPreparacion.EN_PREPARACION, resultado.get().getEstado());
        assertEquals(0, new BigDecimal("90.000").compareTo(insumoPrueba.getStock()));
        verify(insumoRepository, times(1)).save(insumoPrueba);
    }

    @Test
    void iniciarPreparacion_DebeLanzarExcepcion_CuandoNoEstaPendiente() {
        preparacionPrueba.setEstado(EstadoPreparacion.ENTREGADA);
        when(preparacionRepository.findById(1L)).thenReturn(Optional.of(preparacionPrueba));

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            preparacionService.iniciarPreparacion(1L);
        });

        assertTrue(excepcion.getMessage().contains("Solo se puede iniciar una preparacion PENDIENTE"));
        verify(insumoRepository, never()).save(any(Insumo.class));
    }

    @Test
    void anular_DebeDevolverElStock_CuandoYaEstabaEnPreparacion() {
        preparacionPrueba.setEstado(EstadoPreparacion.EN_PREPARACION);
        insumoPrueba.setStock(new BigDecimal("90.000"));
        when(preparacionRepository.findById(1L)).thenReturn(Optional.of(preparacionPrueba));
        when(componenteRepository.findByFormulaId(30L)).thenReturn(List.of(componentePrueba));
        when(insumoRepository.findById(40L)).thenReturn(Optional.of(insumoPrueba));
        when(preparacionRepository.save(any(Preparacion.class))).thenReturn(preparacionPrueba);

        Optional<Preparacion> resultado = preparacionService.anular(1L);

        assertTrue(resultado.isPresent());
        assertEquals(EstadoPreparacion.ANULADA, resultado.get().getEstado());
        assertEquals(0, new BigDecimal("100.000").compareTo(insumoPrueba.getStock()));
    }

    @Test
    void entregar_DebeLanzarExcepcion_CuandoLaPreparacionNoEstaLista() {
        preparacionPrueba.setEstado(EstadoPreparacion.PENDIENTE);
        when(preparacionRepository.findById(1L)).thenReturn(Optional.of(preparacionPrueba));

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            preparacionService.entregar(1L);
        });

        assertTrue(excepcion.getMessage().contains("Solo se puede entregar una preparacion LISTA"));
    }

    @Test
    void eliminarPreparacion_DebeLanzarExcepcion_CuandoYaFueEntregada() {
        preparacionPrueba.setEstado(EstadoPreparacion.ENTREGADA);
        when(preparacionRepository.findById(1L)).thenReturn(Optional.of(preparacionPrueba));

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            preparacionService.eliminarPreparacion(1L);
        });

        assertTrue(excepcion.getMessage().contains("No se puede eliminar una preparacion ya entregada"));
        verify(preparacionRepository, never()).deleteById(1L);
    }

    private void prepararEscenarioValido() {
        when(pacienteRepository.existsById(10L)).thenReturn(true);
        when(medicoRepository.existsById(20L)).thenReturn(true);
        when(formulaRepository.findById(30L)).thenReturn(Optional.of(formulaPrueba));
        when(componenteRepository.findByFormulaId(30L)).thenReturn(List.of(componentePrueba));
        when(insumoRepository.findById(40L)).thenReturn(Optional.of(insumoPrueba));
    }
}
