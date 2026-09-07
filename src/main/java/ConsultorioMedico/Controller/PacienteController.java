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

import ConsultorioMedico.model.Paciente;
import ConsultorioMedico.service.PacienteService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping("/guardar")
    public ResponseEntity<?> guardar(@Valid @RequestBody Paciente paciente) {
        try {
            Paciente nuevo = pacienteService.registrarPaciente(paciente);
            return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Paciente>> listar() {
        return new ResponseEntity<>(pacienteService.listarPacientes(), HttpStatus.OK);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Paciente> buscar(@PathVariable Long id) {
        return pacienteService.buscarPorId(id)
                .map(paciente -> new ResponseEntity<>(paciente, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Paciente paciente) {
        try {
            return pacienteService.actualizarPaciente(id, paciente)
                    .map(actualizado -> new ResponseEntity<>(actualizado, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            if (pacienteService.eliminarPaciente(id)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}

