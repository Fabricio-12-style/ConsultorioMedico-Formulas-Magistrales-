package ConsultorioMedico.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ConsultorioMedico.model.Insumo;
import ConsultorioMedico.model.TipoInsumo;
import ConsultorioMedico.service.InsumoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/insumos")
public class InsumoController {

    private final InsumoService insumoService;

    public InsumoController(InsumoService insumoService) {
        this.insumoService = insumoService;
    }

    @PostMapping("/guardar")
    public ResponseEntity<?> guardar(@Valid @RequestBody Insumo insumo) {
        try {
            Insumo nuevo = insumoService.registrarInsumo(insumo);
            return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Insumo>> listar() {
        return new ResponseEntity<>(insumoService.listarInsumos(), HttpStatus.OK);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Insumo> buscar(@PathVariable Long id) {
        return insumoService.buscarPorId(id)
                .map(insumo -> new ResponseEntity<>(insumo, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Insumo>> listarPorTipo(@PathVariable TipoInsumo tipo) {
        return new ResponseEntity<>(insumoService.listarPorTipo(tipo), HttpStatus.OK);
    }

    @GetMapping("/vencidos")
    public ResponseEntity<List<Insumo>> listarVencidos() {
        return new ResponseEntity<>(insumoService.listarVencidos(), HttpStatus.OK);
    }

    @GetMapping("/bajo-stock")
    public ResponseEntity<List<Insumo>> listarBajoStock() {
        return new ResponseEntity<>(insumoService.listarBajoStock(), HttpStatus.OK);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Insumo insumo) {
        try {
            return insumoService.actualizarInsumo(id, insumo)
                    .map(actualizado -> new ResponseEntity<>(actualizado, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            if (insumoService.eliminarInsumo(id)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}