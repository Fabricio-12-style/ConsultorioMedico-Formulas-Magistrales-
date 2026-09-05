package ConsultorioMedico.Controller;

import ConsultorioMedico.model.CitaMedica;
import ConsultorioMedico.service.CitaMedicaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaMedicaController {

    private final CitaMedicaService citaService;

    public CitaMedicaController(CitaMedicaService citaService) {
        this.citaService = citaService;
    }

    @PostMapping
    public ResponseEntity<?> registrarCita(@RequestBody CitaMedica cita) {
        try {
            CitaMedica nuevaCita = citaService.registrarCita(cita);
            return new ResponseEntity<>(nuevaCita, HttpStatus.CREATED); // 201
        } catch (IllegalArgumentException e) {
     
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<CitaMedica>> listarCitas() {
        return new ResponseEntity<>(citaService.listarCitas(), HttpStatus.OK); // 200
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaMedica> buscarCita(@PathVariable Long id) {
        return citaService.buscarPorId(id)
                .map(cita -> new ResponseEntity<>(cita, HttpStatus.OK)) // 200 si la encuentra
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND)); // 404 si no existe
    }
}