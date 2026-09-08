package ConsultorioMedico.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ConsultorioMedico.model.ComponenteFormula;
import ConsultorioMedico.model.FormulaMagistral;
import ConsultorioMedico.model.Insumo;
import ConsultorioMedico.repository.ComponenteFormulaRepository;
import ConsultorioMedico.repository.FormulaMagistralRepository;
import ConsultorioMedico.repository.InsumoRepository;
import ConsultorioMedico.repository.PreparacionRepository;

@Service
public class FormulaMagistralService {

    private final FormulaMagistralRepository formulaRepository;
    private final ComponenteFormulaRepository componenteRepository;
    private final InsumoRepository insumoRepository;
    private final PreparacionRepository preparacionRepository;

    public FormulaMagistralService(FormulaMagistralRepository formulaRepository,
            ComponenteFormulaRepository componenteRepository,
            InsumoRepository insumoRepository,
            PreparacionRepository preparacionRepository) {
        this.formulaRepository = formulaRepository;
        this.componenteRepository = componenteRepository;
        this.insumoRepository = insumoRepository;
        this.preparacionRepository = preparacionRepository;
    }

    public FormulaMagistral registrarFormula(FormulaMagistral formula) {
        if (formulaRepository.existsByCodigo(formula.getCodigo())) {
            throw new IllegalArgumentException(
                    "Error: Ya existe una formula con el codigo " + formula.getCodigo() + ".");
        }
        if (formula.getActivo() == null) {
            formula.setActivo(true);
        }
        return formulaRepository.save(formula);
    }

    public Optional<FormulaMagistral> actualizarFormula(Long id, FormulaMagistral datos) {
        return formulaRepository.findById(id).map(formula -> {
            if (!formula.getCodigo().equals(datos.getCodigo())
                    && formulaRepository.existsByCodigo(datos.getCodigo())) {
                throw new IllegalArgumentException(
                        "Error: Ya existe una formula con el codigo " + datos.getCodigo() + ".");
            }
            formula.setCodigo(datos.getCodigo());
            formula.setNombre(datos.getNombre());
            formula.setFormaFarmaceutica(datos.getFormaFarmaceutica());
            formula.setViaAdministracion(datos.getViaAdministracion());
            formula.setDescripcion(datos.getDescripcion());
            formula.setIndicaciones(datos.getIndicaciones());
            if (datos.getActivo() != null) {
                formula.setActivo(datos.getActivo());
            }
            return formulaRepository.save(formula);
        });
    }

    public boolean eliminarFormula(Long id) {
        if (!formulaRepository.existsById(id)) {
            return false;
        }
        if (preparacionRepository.existsByFormulaId(id)) {
            throw new IllegalArgumentException(
                    "Error: No se puede eliminar la formula porque tiene preparaciones registradas.");
        }
        componenteRepository.deleteAll(componenteRepository.findByFormulaId(id));
        formulaRepository.deleteById(id);
        return true;
    }

    public List<FormulaMagistral> listarFormulas() {
        return formulaRepository.findAllByOrderByNombreAsc();
    }

    public List<FormulaMagistral> listarActivas() {
        return formulaRepository.findByActivoTrue();
    }

    public Optional<FormulaMagistral> buscarPorId(Long id) {
        return formulaRepository.findById(id);
    }

    public ComponenteFormula agregarInsumo(Long formulaId, ComponenteFormula componente) {
        if (!formulaRepository.existsById(formulaId)) {
            throw new IllegalArgumentException("Error: La formula con ID " + formulaId + " no existe.");
        }
        Insumo insumo = insumoRepository.findById(componente.getInsumoId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Error: El insumo con ID " + componente.getInsumoId() + " no existe."));

        if (componenteRepository.existsByFormulaIdAndInsumoId(formulaId, componente.getInsumoId())) {
            throw new IllegalArgumentException(
                    "Error: El insumo " + insumo.getNombre() + " ya forma parte de la formula.");
        }
        if (componente.getCantidad() == null || componente.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Error: La cantidad debe ser mayor a cero.");
        }
        if (!insumo.getUnidadMedida().equalsIgnoreCase(componente.getUnidadMedida())) {
            throw new IllegalArgumentException("Error: La unidad de medida debe ser "
                    + insumo.getUnidadMedida() + ", que es la del insumo " + insumo.getNombre() + ".");
        }
        componente.setFormulaId(formulaId);
        return componenteRepository.save(componente);
    }

    public List<ComponenteFormula> listarComponentes(Long formulaId) {
        return componenteRepository.findByFormulaId(formulaId);
    }

    public boolean quitarInsumo(Long componenteId) {
        Optional<ComponenteFormula> componente = componenteRepository.findById(componenteId);
        if (componente.isEmpty()) {
            return false;
        }
        if (preparacionRepository.existsByFormulaId(componente.get().getFormulaId())) {
            throw new IllegalArgumentException(
                    "Error: No se puede modificar la composicion de una formula que ya tiene preparaciones.");
        }
        componenteRepository.deleteById(componenteId);
        return true;
    }
}