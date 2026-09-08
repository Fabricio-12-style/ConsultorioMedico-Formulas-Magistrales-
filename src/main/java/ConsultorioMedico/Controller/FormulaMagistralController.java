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

import ConsultorioMedico.model.ComponenteFormula;
import ConsultorioMedico.model.FormulaMagistral;
import ConsultorioMedico.service.FormulaMagistralService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/formulas")
public class FormulaMagistralController {

    private final FormulaMagistralService formulaService;

    public FormulaMagistralController(FormulaMagistralService formulaService) {
        this.formulaService = formulaService;
    }

    @PostMapping("/guardar")
    public ResponseEntity<?> guardar(@Valid @RequestBody FormulaMagistral formula) {
        try {
            FormulaMagistral nueva = formulaService.registrarFormula(formula);
            return new ResponseEntity<>(nueva, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }   

    @GetMapping("/listar")
    public ResponseEntity<List<FormulaMagistral>> listar() {
        return new ResponseEntity<>(formulaService.listarFormulas(), HttpStatus.OK);
    }

    @GetMapping("/activas")
    public ResponseEntity<List<FormulaMagistral>> listarActivas() {
        return new ResponseEntity<>(formulaService.listarActivas(), HttpStatus.OK);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<FormulaMagistral> buscar(@PathVariable Long id) {
        return formulaService.buscarPorId(id)
                .map(formula -> new ResponseEntity<>(formula, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody FormulaMagistral formula) {
        try {
            return formulaService.actualizarFormula(id, formula)
                    .map(actualizada -> new ResponseEntity<>(actualizada, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            if (formulaService.eliminarFormula(id)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/agregar-insumo/{formulaId}")
    public ResponseEntity<?> agregarInsumo(@PathVariable Long formulaId,
            @Valid @RequestBody ComponenteFormula componente) {
        try {
            ComponenteFormula nuevo = formulaService.agregarInsumo(formulaId, componente);
            return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/insumos/{formulaId}")
    public ResponseEntity<List<ComponenteFormula>> listarComponentes(@PathVariable Long formulaId) {
        return new ResponseEntity<>(formulaService.listarComponentes(formulaId), HttpStatus.OK);
    }

    @DeleteMapping("/quitar-insumo/{componenteId}")
    public ResponseEntity<?> quitarInsumo(@PathVariable Long componenteId) {
        try {
            if (formulaService.quitarInsumo(componenteId)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}