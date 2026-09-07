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

import ConsultorioMedico.model.CitaMedica;
import ConsultorioMedico.service.CitaMedicaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/citas")
public class CitaMedicaController {

    private final CitaMedicaService citaService;

    public CitaMedicaController(CitaMedicaService citaService) {
        this.citaService = citaService;
    }

    @PostMapping("/guardar")
    public ResponseEntity<?> guardar(@Valid @RequestBody CitaMedica cita) {
        try {
            CitaMedica nuevaCita = citaService.registrarCita(cita);
            return new ResponseEntity<>(nuevaCita, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<CitaMedica>> listar() {
        return new ResponseEntity<>(citaService.listarCitas(), HttpStatus.OK);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<CitaMedica> buscar(@PathVariable Long id) {
        return citaService.buscarPorId(id)
                .map(cita -> new ResponseEntity<>(cita, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody CitaMedica cita) {
        try {
            return citaService.actualizarCita(id, cita)
                    .map(actualizada -> new ResponseEntity<>(actualizada, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (citaService.eliminarCita(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
