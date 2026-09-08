package ConsultorioMedico.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ConsultorioMedico.model.Insumo;
import ConsultorioMedico.model.TipoInsumo;
import ConsultorioMedico.repository.ComponenteFormulaRepository;
import ConsultorioMedico.repository.InsumoRepository;

@Service
public class InsumoService {

    private final InsumoRepository insumoRepository;
    private final ComponenteFormulaRepository componenteRepository;

    public InsumoService(InsumoRepository insumoRepository, ComponenteFormulaRepository componenteRepository) {
        this.insumoRepository = insumoRepository;
        this.componenteRepository = componenteRepository;
    }

    public Insumo registrarInsumo(Insumo insumo) {
        if (insumoRepository.existsByCodigo(insumo.getCodigo())) {
            throw new IllegalArgumentException(
                    "Error: Ya existe un insumo con el codigo " + insumo.getCodigo() + ".");
        }
        if (insumo.getFechaVencimiento().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Error: No se puede registrar un insumo ya vencido el " + insumo.getFechaVencimiento() + ".");
        }
        return insumoRepository.save(insumo);
    }

    public Optional<Insumo> actualizarInsumo(Long id, Insumo datos) {
        return insumoRepository.findById(id).map(insumo -> {
            if (!insumo.getCodigo().equals(datos.getCodigo())
                    && insumoRepository.existsByCodigo(datos.getCodigo())) {
                throw new IllegalArgumentException(
                        "Error: Ya existe un insumo con el codigo " + datos.getCodigo() + ".");
            }
            insumo.setCodigo(datos.getCodigo());
            insumo.setNombre(datos.getNombre());
            insumo.setTipo(datos.getTipo());
            insumo.setUnidadMedida(datos.getUnidadMedida());
            insumo.setStock(datos.getStock());
            insumo.setStockMinimo(datos.getStockMinimo());
            insumo.setPrecioUnitario(datos.getPrecioUnitario());
            insumo.setLote(datos.getLote());
            insumo.setFechaVencimiento(datos.getFechaVencimiento());
            return insumoRepository.save(insumo);
        });
    }

    public boolean eliminarInsumo(Long id) {
        if (!insumoRepository.existsById(id)) {
            return false;
        }
        if (componenteRepository.existsByInsumoId(id)) {
            throw new IllegalArgumentException(
                    "Error: No se puede eliminar el insumo porque forma parte de una formula.");
        }
        insumoRepository.deleteById(id);
        return true;
    }

    public List<Insumo> listarInsumos() {
        return insumoRepository.findAllByOrderByNombreAsc();
    }

    public List<Insumo> listarPorTipo(TipoInsumo tipo) {
        return insumoRepository.findByTipo(tipo);
    }

    public List<Insumo> listarVencidos() {
        return insumoRepository.findByFechaVencimientoBefore(LocalDate.now());
    }

    public List<Insumo> listarBajoStock() {
        return insumoRepository.findAll().stream()
                .filter(insumo -> insumo.getStock().compareTo(insumo.getStockMinimo()) < 0)
                .collect(Collectors.toList());
    }

    public Optional<Insumo> buscarPorId(Long id) {
        return insumoRepository.findById(id);
    }
}