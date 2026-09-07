package ConsultorioMedico.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ConsultorioMedico.model.ComponenteFormula;
import ConsultorioMedico.model.EstadoPreparacion;
import ConsultorioMedico.model.FormulaMagistral;
import ConsultorioMedico.model.Insumo;
import ConsultorioMedico.model.Preparacion;
import ConsultorioMedico.repository.ComponenteFormulaRepository;
import ConsultorioMedico.repository.FormulaMagistralRepository;
import ConsultorioMedico.repository.InsumoRepository;
import ConsultorioMedico.repository.MedicoRepository;
import ConsultorioMedico.repository.PacienteRepository;
import ConsultorioMedico.repository.PreparacionRepository;

@Service
public class PreparacionService {

    private final PreparacionRepository preparacionRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final FormulaMagistralRepository formulaRepository;
    private final ComponenteFormulaRepository componenteRepository;
    private final InsumoRepository insumoRepository;

    public PreparacionService(PreparacionRepository preparacionRepository,
            PacienteRepository pacienteRepository,
            MedicoRepository medicoRepository,
            FormulaMagistralRepository formulaRepository,
            ComponenteFormulaRepository componenteRepository,
            InsumoRepository insumoRepository) {
        this.preparacionRepository = preparacionRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.formulaRepository = formulaRepository;
        this.componenteRepository = componenteRepository;
        this.insumoRepository = insumoRepository;
    }

    public Preparacion registrarPreparacion(Preparacion preparacion) {
        validarReferencias(preparacion);
        validarDisponibilidad(preparacion.getFormulaId(), preparacion.getCantidad());

        preparacion.setFechaSolicitud(LocalDateTime.now());
        preparacion.setEstado(EstadoPreparacion.PENDIENTE);
        return preparacionRepository.save(preparacion);
    }

    public Optional<Preparacion> actualizarPreparacion(Long id, Preparacion datos) {
        return preparacionRepository.findById(id).map(preparacion -> {
            if (preparacion.getEstado() == EstadoPreparacion.ENTREGADA) {
                throw new IllegalArgumentException("Error: No se puede modificar una preparacion ya entregada.");
            }
            validarReferencias(datos);
            validarDisponibilidad(datos.getFormulaId(), datos.getCantidad());

            preparacion.setCitaId(datos.getCitaId());
            preparacion.setPacienteId(datos.getPacienteId());
            preparacion.setMedicoId(datos.getMedicoId());
            preparacion.setFormulaId(datos.getFormulaId());
            preparacion.setCantidad(datos.getCantidad());
            preparacion.setFechaEntrega(datos.getFechaEntrega());
            preparacion.setObservaciones(datos.getObservaciones());
            return preparacionRepository.save(preparacion);
        });
    }

    public boolean eliminarPreparacion(Long id) {
        Optional<Preparacion> preparacion = preparacionRepository.findById(id);
        if (preparacion.isEmpty()) {
            return false;
        }
        if (preparacion.get().getEstado() == EstadoPreparacion.ENTREGADA) {
            throw new IllegalArgumentException(
                    "Error: No se puede eliminar una preparacion ya entregada, debe quedar como historial.");
        }
        preparacionRepository.deleteById(id);
        return true;
    }

    @Transactional
    public Optional<Preparacion> iniciarPreparacion(Long id) {
        return preparacionRepository.findById(id).map(preparacion -> {
            if (preparacion.getEstado() != EstadoPreparacion.PENDIENTE) {
                throw new IllegalArgumentException(
                        "Error: Solo se puede iniciar una preparacion PENDIENTE. Estado actual: "
                                + preparacion.getEstado() + ".");
            }
            validarDisponibilidad(preparacion.getFormulaId(), preparacion.getCantidad());
            moverStock(preparacion.getFormulaId(), preparacion.getCantidad(), true);

            preparacion.setEstado(EstadoPreparacion.EN_PREPARACION);
            return preparacionRepository.save(preparacion);
        });
    }

    public Optional<Preparacion> marcarLista(Long id) {
        return preparacionRepository.findById(id).map(preparacion -> {
            if (preparacion.getEstado() != EstadoPreparacion.EN_PREPARACION) {
                throw new IllegalArgumentException(
                        "Error: Solo se puede marcar como LISTA una preparacion EN_PREPARACION. Estado actual: "
                                + preparacion.getEstado() + ".");
            }
            preparacion.setEstado(EstadoPreparacion.LISTA);
            return preparacionRepository.save(preparacion);
        });
    }

    public Optional<Preparacion> entregar(Long id) {
        return preparacionRepository.findById(id).map(preparacion -> {
            if (preparacion.getEstado() != EstadoPreparacion.LISTA) {
                throw new IllegalArgumentException(
                        "Error: Solo se puede entregar una preparacion LISTA. Estado actual: "
                                + preparacion.getEstado() + ".");
            }
            preparacion.setEstado(EstadoPreparacion.ENTREGADA);
            preparacion.setFechaEntrega(LocalDateTime.now());
            return preparacionRepository.save(preparacion);
        });
    }

    @Transactional
    public Optional<Preparacion> anular(Long id) {
        return preparacionRepository.findById(id).map(preparacion -> {
            if (preparacion.getEstado() == EstadoPreparacion.ENTREGADA) {
                throw new IllegalArgumentException("Error: No se puede anular una preparacion ya entregada.");
            }
            if (preparacion.getEstado() == EstadoPreparacion.ANULADA) {
                throw new IllegalArgumentException("Error: La preparacion ya estaba anulada.");
            }
            boolean stockYaConsumido = preparacion.getEstado() == EstadoPreparacion.EN_PREPARACION
                    || preparacion.getEstado() == EstadoPreparacion.LISTA;
            if (stockYaConsumido) {
                moverStock(preparacion.getFormulaId(), preparacion.getCantidad(), false);
            }
            preparacion.setEstado(EstadoPreparacion.ANULADA);
            return preparacionRepository.save(preparacion);
        });
    }

    public List<Preparacion> listarPreparaciones() {
        return preparacionRepository.findAllByOrderByFechaSolicitudDesc();
    }

    public List<Preparacion> listarPorEstado(EstadoPreparacion estado) {
        return preparacionRepository.findByEstado(estado);
    }

    public Optional<Preparacion> buscarPorId(Long id) {
        return preparacionRepository.findById(id);
    }

    private void validarReferencias(Preparacion preparacion) {
        if (!pacienteRepository.existsById(preparacion.getPacienteId())) {
            throw new IllegalArgumentException(
                    "Error: El paciente con ID " + preparacion.getPacienteId() + " no existe.");
        }
        if (!medicoRepository.existsById(preparacion.getMedicoId())) {
            throw new IllegalArgumentException(
                    "Error: El medico con ID " + preparacion.getMedicoId() + " no existe.");
        }
        FormulaMagistral formula = formulaRepository.findById(preparacion.getFormulaId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Error: La formula con ID " + preparacion.getFormulaId() + " no existe."));
        if (Boolean.FALSE.equals(formula.getActivo())) {
            throw new IllegalArgumentException(
                    "Error: La formula " + formula.getNombre() + " esta descontinuada.");
        }
    }

    private void validarDisponibilidad(Long formulaId, Integer cantidad) {
        List<ComponenteFormula> componentes = componenteRepository.findByFormulaId(formulaId);
        if (componentes.isEmpty()) {
            throw new IllegalArgumentException(
                    "Error: La formula con ID " + formulaId + " no tiene insumos registrados.");
        }
        for (ComponenteFormula componente : componentes) {
            Insumo insumo = buscarInsumo(componente.getInsumoId());

            if (insumo.getFechaVencimiento().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Error: El insumo " + insumo.getNombre() + " (lote "
                        + insumo.getLote() + ") esta vencido desde el " + insumo.getFechaVencimiento() + ".");
            }
            BigDecimal requerido = componente.getCantidad().multiply(BigDecimal.valueOf(cantidad));
            if (insumo.getStock().compareTo(requerido) < 0) {
                throw new IllegalArgumentException("Error: Stock insuficiente de " + insumo.getNombre()
                        + ". Se necesitan " + requerido + " " + insumo.getUnidadMedida()
                        + " y solo hay " + insumo.getStock() + " " + insumo.getUnidadMedida() + ".");
            }
        }
    }

    private void moverStock(Long formulaId, Integer cantidad, boolean consumir) {
        for (ComponenteFormula componente : componenteRepository.findByFormulaId(formulaId)) {
            Insumo insumo = buscarInsumo(componente.getInsumoId());
            BigDecimal movimiento = componente.getCantidad().multiply(BigDecimal.valueOf(cantidad));

            if (consumir) {
                insumo.setStock(insumo.getStock().subtract(movimiento));
            } else {
                insumo.setStock(insumo.getStock().add(movimiento));
            }
            insumoRepository.save(insumo);
        }
    }

    private Insumo buscarInsumo(Long insumoId) {
        return insumoRepository.findById(insumoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Error: El insumo con ID " + insumoId + " no existe."));
    }
}
