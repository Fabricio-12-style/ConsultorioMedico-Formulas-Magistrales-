package ConsultorioMedico.Controller;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

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

import ConsultorioMedico.model.EstadoPreparacion;
import ConsultorioMedico.model.Preparacion;
import ConsultorioMedico.service.PreparacionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/preparaciones")
public class PreparacionController {

    private final PreparacionService preparacionService;

    public PreparacionController(PreparacionService preparacionService) {
        this.preparacionService = preparacionService;
    }

    @PostMapping("/guardar")
    public ResponseEntity<?> guardar(@Valid @RequestBody Preparacion preparacion) {
        try {
            Preparacion nueva = preparacionService.registrarPreparacion(preparacion);
            return new ResponseEntity<>(nueva, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Preparacion>> listar() {
        return new ResponseEntity<>(preparacionService.listarPreparaciones(), HttpStatus.OK);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Preparacion> buscar(@PathVariable Long id) {
        return preparacionService.buscarPorId(id)
                .map(preparacion -> new ResponseEntity<>(preparacion, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Preparacion>> listarPorEstado(@PathVariable EstadoPreparacion estado) {
        return new ResponseEntity<>(preparacionService.listarPorEstado(estado), HttpStatus.OK);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Preparacion preparacion) {
        try {
            return preparacionService.actualizarPreparacion(id, preparacion)
                    .map(actualizada -> new ResponseEntity<>(actualizada, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            if (preparacionService.eliminarPreparacion(id)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/iniciar/{id}")
    public ResponseEntity<?> iniciar(@PathVariable Long id) {
        return cambiarEstado(() -> preparacionService.iniciarPreparacion(id));
    }

    @PutMapping("/marcar-lista/{id}")
    public ResponseEntity<?> marcarLista(@PathVariable Long id) {
        return cambiarEstado(() -> preparacionService.marcarLista(id));
    }

    @PutMapping("/entregar/{id}")
    public ResponseEntity<?> entregar(@PathVariable Long id) {
        return cambiarEstado(() -> preparacionService.entregar(id));
    }

    @PutMapping("/anular/{id}")
    public ResponseEntity<?> anular(@PathVariable Long id) {
        return cambiarEstado(() -> preparacionService.anular(id));
    }

    private ResponseEntity<?> cambiarEstado(Supplier<Optional<Preparacion>> transicion) {
        try {
            return transicion.get()
                    .map(preparacion -> new ResponseEntity<>(preparacion, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
