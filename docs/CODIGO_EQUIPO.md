# Codigo del equipo - Avance 01

Todo este codigo ya fue compilado y probado sobre la rama `main` (commit `fae70c7`): `mvn test` -> **41 tests, 0 fallos, BUILD SUCCESS**.

Cada quien crea los archivos en su propia rama, en la ruta exacta que dice cada titulo, y pega el contenido tal cual.

**Antes de empezar:** MySQL corriendo con la base `formulasMagistrales`, y `git checkout main && git pull` para partir del codigo actualizado.

---

# JEAN - rama `jean`

```bash
git checkout main
git pull
git checkout -b jean
```

6 archivos nuevos. Endpoints: `/api/pacientes` y `/api/medicos`.

### `src/main/java/ConsultorioMedico/service/PacienteService.java`

```java
package ConsultorioMedico.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ConsultorioMedico.model.Paciente;
import ConsultorioMedico.repository.CitaMedicaRepository;
import ConsultorioMedico.repository.PacienteRepository;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final CitaMedicaRepository citaRepository;

    public PacienteService(PacienteRepository pacienteRepository, CitaMedicaRepository citaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.citaRepository = citaRepository;
    }

    public Paciente registrarPaciente(Paciente paciente) {
        if (pacienteRepository.existsByDni(paciente.getDni())) {
            throw new IllegalArgumentException(
                    "Error: Ya existe un paciente registrado con el DNI " + paciente.getDni() + ".");
        }
        return pacienteRepository.save(paciente);
    }

    public Optional<Paciente> actualizarPaciente(Long id, Paciente datos) {
        return pacienteRepository.findById(id).map(paciente -> {
            if (!paciente.getDni().equals(datos.getDni()) && pacienteRepository.existsByDni(datos.getDni())) {
                throw new IllegalArgumentException(
                        "Error: Ya existe un paciente registrado con el DNI " + datos.getDni() + ".");
            }
            paciente.setNombre(datos.getNombre());
            paciente.setDni(datos.getDni());
            paciente.setTelefono(datos.getTelefono());
            paciente.setEmail(datos.getEmail());
            return pacienteRepository.save(paciente);
        });
    }

    public boolean eliminarPaciente(Long id) {
        if (!pacienteRepository.existsById(id)) {
            return false;
        }
        if (citaRepository.existsByPacienteId(id)) {
            throw new IllegalArgumentException(
                    "Error: No se puede eliminar el paciente porque tiene citas registradas.");
        }
        pacienteRepository.deleteById(id);
        return true;
    }

    public List<Paciente> listarPacientes() {
        return pacienteRepository.findAll();
    }

    public Optional<Paciente> buscarPorId(Long id) {
        return pacienteRepository.findById(id);
    }
}
```

### `src/main/java/ConsultorioMedico/Controller/PacienteController.java`

```java
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
```

### `src/main/java/ConsultorioMedico/service/MedicoService.java`

```java
package ConsultorioMedico.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ConsultorioMedico.model.Medico;
import ConsultorioMedico.repository.CitaMedicaRepository;
import ConsultorioMedico.repository.MedicoRepository;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;
    private final CitaMedicaRepository citaRepository;

    public MedicoService(MedicoRepository medicoRepository, CitaMedicaRepository citaRepository) {
        this.medicoRepository = medicoRepository;
        this.citaRepository = citaRepository;
    }

    public Medico registrarMedico(Medico medico) {
        if (medicoRepository.existsByCmp(medico.getCmp())) {
            throw new IllegalArgumentException(
                    "Error: Ya existe un medico registrado con el CMP " + medico.getCmp() + ".");
        }
        return medicoRepository.save(medico);
    }

    public Optional<Medico> actualizarMedico(Long id, Medico datos) {
        return medicoRepository.findById(id).map(medico -> {
            if (!medico.getCmp().equals(datos.getCmp()) && medicoRepository.existsByCmp(datos.getCmp())) {
                throw new IllegalArgumentException(
                        "Error: Ya existe un medico registrado con el CMP " + datos.getCmp() + ".");
            }
            medico.setNombre(datos.getNombre());
            medico.setCmp(datos.getCmp());
            medico.setEspecialidad(datos.getEspecialidad());
            return medicoRepository.save(medico);
        });
    }

    public boolean eliminarMedico(Long id) {
        if (!medicoRepository.existsById(id)) {
            return false;
        }
        if (citaRepository.existsByMedicoId(id)) {
            throw new IllegalArgumentException(
                    "Error: No se puede eliminar el medico porque tiene citas registradas.");
        }
        medicoRepository.deleteById(id);
        return true;
    }

    public List<Medico> listarMedicos() {
        return medicoRepository.findAll();
    }

    public Optional<Medico> buscarPorId(Long id) {
        return medicoRepository.findById(id);
    }
}
```

### `src/main/java/ConsultorioMedico/Controller/MedicoController.java`

```java
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

import ConsultorioMedico.model.Medico;
import ConsultorioMedico.service.MedicoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    private final MedicoService medicoService;

    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    @PostMapping("/guardar")
    public ResponseEntity<?> guardar(@Valid @RequestBody Medico medico) {
        try {
            Medico nuevo = medicoService.registrarMedico(medico);
            return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Medico>> listar() {
        return new ResponseEntity<>(medicoService.listarMedicos(), HttpStatus.OK);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Medico> buscar(@PathVariable Long id) {
        return medicoService.buscarPorId(id)
                .map(medico -> new ResponseEntity<>(medico, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Medico medico) {
        try {
            return medicoService.actualizarMedico(id, medico)
                    .map(actualizado -> new ResponseEntity<>(actualizado, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            if (medicoService.eliminarMedico(id)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
```

### `src/test/java/ConsultorioMedico/PacienteServiceTest.java`

```java
package ConsultorioMedico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ConsultorioMedico.model.Paciente;
import ConsultorioMedico.repository.CitaMedicaRepository;
import ConsultorioMedico.repository.PacienteRepository;
import ConsultorioMedico.service.PacienteService;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private CitaMedicaRepository citaRepository;

    @InjectMocks
    private PacienteService pacienteService;

    private Paciente pacientePrueba;

    @BeforeEach
    void setUp() {
        pacientePrueba = new Paciente();
        pacientePrueba.setId(1L);
        pacientePrueba.setNombre("Juan Perez");
        pacientePrueba.setDni("12345678");
        pacientePrueba.setTelefono("987654321");
        pacientePrueba.setEmail("juan.perez@example.com");
    }

    @Test
    void registrarPaciente_DebeLanzarExcepcion_CuandoElDniYaExiste() {
        when(pacienteRepository.existsByDni("12345678")).thenReturn(true);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            pacienteService.registrarPaciente(pacientePrueba);
        });

        assertTrue(excepcion.getMessage().contains("Ya existe un paciente registrado con el DNI 12345678"));
        verify(pacienteRepository, never()).save(any(Paciente.class));
    }

    @Test
    void registrarPaciente_DebeGuardar_CuandoElDniEsNuevo() {
        when(pacienteRepository.existsByDni("12345678")).thenReturn(false);
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacientePrueba);

        Paciente resultado = pacienteService.registrarPaciente(pacientePrueba);

        assertEquals("Juan Perez", resultado.getNombre());
        verify(pacienteRepository, times(1)).save(pacientePrueba);
    }

    @Test
    void eliminarPaciente_DebeLanzarExcepcion_CuandoTieneCitasRegistradas() {
        when(pacienteRepository.existsById(1L)).thenReturn(true);
        when(citaRepository.existsByPacienteId(1L)).thenReturn(true);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            pacienteService.eliminarPaciente(1L);
        });

        assertTrue(excepcion.getMessage().contains("tiene citas registradas"));
        verify(pacienteRepository, never()).deleteById(anyLong());
    }

    @Test
    void eliminarPaciente_DebeDevolverFalse_CuandoNoExiste() {
        when(pacienteRepository.existsById(99L)).thenReturn(false);

        assertFalse(pacienteService.eliminarPaciente(99L));

        verify(pacienteRepository, never()).deleteById(anyLong());
    }

    @Test
    void eliminarPaciente_DebeEliminar_CuandoNoTieneCitas() {
        when(pacienteRepository.existsById(1L)).thenReturn(true);
        when(citaRepository.existsByPacienteId(1L)).thenReturn(false);

        assertTrue(pacienteService.eliminarPaciente(1L));

        verify(pacienteRepository, times(1)).deleteById(1L);
    }
}
```

### `src/test/java/ConsultorioMedico/MedicoServiceTest.java`

```java
package ConsultorioMedico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ConsultorioMedico.model.Medico;
import ConsultorioMedico.repository.CitaMedicaRepository;
import ConsultorioMedico.repository.MedicoRepository;
import ConsultorioMedico.service.MedicoService;

@ExtendWith(MockitoExtension.class)
class MedicoServiceTest {

    @Mock
    private MedicoRepository medicoRepository;

    @Mock
    private CitaMedicaRepository citaRepository;

    @InjectMocks
    private MedicoService medicoService;

    private Medico medicoPrueba;

    @BeforeEach
    void setUp() {
        medicoPrueba = new Medico();
        medicoPrueba.setId(1L);
        medicoPrueba.setNombre("Dra. Ana Gomez");
        medicoPrueba.setCmp("CMP-10234");
        medicoPrueba.setEspecialidad("Cardiologia");
    }

    @Test
    void registrarMedico_DebeLanzarExcepcion_CuandoElCmpYaExiste() {
        when(medicoRepository.existsByCmp("CMP-10234")).thenReturn(true);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            medicoService.registrarMedico(medicoPrueba);
        });

        assertTrue(excepcion.getMessage().contains("Ya existe un medico registrado con el CMP CMP-10234"));
        verify(medicoRepository, never()).save(any(Medico.class));
    }

    @Test
    void registrarMedico_DebeGuardar_CuandoElCmpEsNuevo() {
        when(medicoRepository.existsByCmp("CMP-10234")).thenReturn(false);
        when(medicoRepository.save(any(Medico.class))).thenReturn(medicoPrueba);

        Medico resultado = medicoService.registrarMedico(medicoPrueba);

        assertEquals("Cardiologia", resultado.getEspecialidad());
        verify(medicoRepository, times(1)).save(medicoPrueba);
    }

    @Test
    void eliminarMedico_DebeLanzarExcepcion_CuandoTieneCitasRegistradas() {
        when(medicoRepository.existsById(1L)).thenReturn(true);
        when(citaRepository.existsByMedicoId(1L)).thenReturn(true);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            medicoService.eliminarMedico(1L);
        });

        assertTrue(excepcion.getMessage().contains("tiene citas registradas"));
        verify(medicoRepository, never()).deleteById(anyLong());
    }

    @Test
    void eliminarMedico_DebeDevolverFalse_CuandoNoExiste() {
        when(medicoRepository.existsById(99L)).thenReturn(false);

        assertFalse(medicoService.eliminarMedico(99L));

        verify(medicoRepository, never()).deleteById(anyLong());
    }
}
```

---

# FABRICIO - rama `fabricio`

```bash
git checkout main
git pull
git checkout -b fabricio
```

6 archivos nuevos. Endpoints: `/api/formulas` y `/api/insumos`.

### `src/main/java/ConsultorioMedico/service/FormulaMagistralService.java`

```java
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
```

### `src/main/java/ConsultorioMedico/Controller/FormulaMagistralController.java`

```java
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
```

### `src/main/java/ConsultorioMedico/service/InsumoService.java`

```java
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
```

### `src/main/java/ConsultorioMedico/Controller/InsumoController.java`

```java
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
```

### `src/test/java/ConsultorioMedico/FormulaMagistralServiceTest.java`

```java
package ConsultorioMedico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ConsultorioMedico.model.ComponenteFormula;
import ConsultorioMedico.model.FormaFarmaceutica;
import ConsultorioMedico.model.FormulaMagistral;
import ConsultorioMedico.model.Insumo;
import ConsultorioMedico.model.TipoInsumo;
import ConsultorioMedico.repository.ComponenteFormulaRepository;
import ConsultorioMedico.repository.FormulaMagistralRepository;
import ConsultorioMedico.repository.InsumoRepository;
import ConsultorioMedico.repository.PreparacionRepository;
import ConsultorioMedico.service.FormulaMagistralService;

@ExtendWith(MockitoExtension.class)
class FormulaMagistralServiceTest {

    @Mock
    private FormulaMagistralRepository formulaRepository;

    @Mock
    private ComponenteFormulaRepository componenteRepository;

    @Mock
    private InsumoRepository insumoRepository;

    @Mock
    private PreparacionRepository preparacionRepository;

    @InjectMocks
    private FormulaMagistralService formulaService;

    private FormulaMagistral formulaPrueba;
    private Insumo insumoPrueba;
    private ComponenteFormula componentePrueba;

    @BeforeEach
    void setUp() {
        formulaPrueba = new FormulaMagistral();
        formulaPrueba.setId(30L);
        formulaPrueba.setCodigo("FM-001");
        formulaPrueba.setNombre("Crema de hidrocortisona al 2%");
        formulaPrueba.setFormaFarmaceutica(FormaFarmaceutica.CREMA);
        formulaPrueba.setViaAdministracion("TOPICA");
        formulaPrueba.setActivo(true);

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

        componentePrueba = new ComponenteFormula();
        componentePrueba.setInsumoId(40L);
        componentePrueba.setCantidad(new BigDecimal("5.000"));
        componentePrueba.setUnidadMedida("g");
    }

    @Test
    void registrarFormula_DebeLanzarExcepcion_CuandoElCodigoYaExiste() {
        when(formulaRepository.existsByCodigo("FM-001")).thenReturn(true);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            formulaService.registrarFormula(formulaPrueba);
        });

        assertTrue(excepcion.getMessage().contains("Ya existe una formula con el codigo FM-001"));
        verify(formulaRepository, never()).save(any(FormulaMagistral.class));
    }

    @Test
    void registrarFormula_DebeGuardar_CuandoElCodigoEsNuevo() {
        when(formulaRepository.existsByCodigo("FM-001")).thenReturn(false);
        when(formulaRepository.save(any(FormulaMagistral.class))).thenReturn(formulaPrueba);

        FormulaMagistral resultado = formulaService.registrarFormula(formulaPrueba);

        assertEquals("FM-001", resultado.getCodigo());
        verify(formulaRepository, times(1)).save(formulaPrueba);
    }

    @Test
    void eliminarFormula_DebeLanzarExcepcion_CuandoTienePreparaciones() {
        when(formulaRepository.existsById(30L)).thenReturn(true);
        when(preparacionRepository.existsByFormulaId(30L)).thenReturn(true);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            formulaService.eliminarFormula(30L);
        });

        assertTrue(excepcion.getMessage().contains("tiene preparaciones registradas"));
        verify(formulaRepository, never()).deleteById(anyLong());
    }

    @Test
    void agregarInsumo_DebeLanzarExcepcion_CuandoElInsumoYaEstaEnLaFormula() {
        when(formulaRepository.existsById(30L)).thenReturn(true);
        when(insumoRepository.findById(40L)).thenReturn(Optional.of(insumoPrueba));
        when(componenteRepository.existsByFormulaIdAndInsumoId(30L, 40L)).thenReturn(true);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            formulaService.agregarInsumo(30L, componentePrueba);
        });

        assertTrue(excepcion.getMessage().contains("ya forma parte de la formula"));
        verify(componenteRepository, never()).save(any(ComponenteFormula.class));
    }

    @Test
    void agregarInsumo_DebeLanzarExcepcion_CuandoLaUnidadNoCoincide() {
        componentePrueba.setUnidadMedida("ml");
        when(formulaRepository.existsById(30L)).thenReturn(true);
        when(insumoRepository.findById(40L)).thenReturn(Optional.of(insumoPrueba));
        when(componenteRepository.existsByFormulaIdAndInsumoId(30L, 40L)).thenReturn(false);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            formulaService.agregarInsumo(30L, componentePrueba);
        });

        assertTrue(excepcion.getMessage().contains("La unidad de medida debe ser g"));
        verify(componenteRepository, never()).save(any(ComponenteFormula.class));
    }

    @Test
    void agregarInsumo_DebeGuardar_CuandoTodoEsCorrecto() {
        when(formulaRepository.existsById(30L)).thenReturn(true);
        when(insumoRepository.findById(40L)).thenReturn(Optional.of(insumoPrueba));
        when(componenteRepository.existsByFormulaIdAndInsumoId(30L, 40L)).thenReturn(false);
        when(componenteRepository.save(any(ComponenteFormula.class))).thenReturn(componentePrueba);

        ComponenteFormula resultado = formulaService.agregarInsumo(30L, componentePrueba);

        assertEquals(30L, resultado.getFormulaId());
        verify(componenteRepository, times(1)).save(componentePrueba);
    }
}
```

### `src/test/java/ConsultorioMedico/InsumoServiceTest.java`

```java
package ConsultorioMedico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ConsultorioMedico.model.Insumo;
import ConsultorioMedico.model.TipoInsumo;
import ConsultorioMedico.repository.ComponenteFormulaRepository;
import ConsultorioMedico.repository.InsumoRepository;
import ConsultorioMedico.service.InsumoService;

@ExtendWith(MockitoExtension.class)
class InsumoServiceTest {

    @Mock
    private InsumoRepository insumoRepository;

    @Mock
    private ComponenteFormulaRepository componenteRepository;

    @InjectMocks
    private InsumoService insumoService;

    private Insumo insumoPrueba;

    @BeforeEach
    void setUp() {
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
    void registrarInsumo_DebeLanzarExcepcion_CuandoElCodigoYaExiste() {
        when(insumoRepository.existsByCodigo("INS-001")).thenReturn(true);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            insumoService.registrarInsumo(insumoPrueba);
        });

        assertTrue(excepcion.getMessage().contains("Ya existe un insumo con el codigo INS-001"));
        verify(insumoRepository, never()).save(any(Insumo.class));
    }

    @Test
    void registrarInsumo_DebeLanzarExcepcion_CuandoYaEstaVencido() {
        insumoPrueba.setFechaVencimiento(LocalDate.now().minusDays(1));
        when(insumoRepository.existsByCodigo("INS-001")).thenReturn(false);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            insumoService.registrarInsumo(insumoPrueba);
        });

        assertTrue(excepcion.getMessage().contains("No se puede registrar un insumo ya vencido"));
        verify(insumoRepository, never()).save(any(Insumo.class));
    }

    @Test
    void registrarInsumo_DebeGuardar_CuandoTodoEsCorrecto() {
        when(insumoRepository.existsByCodigo("INS-001")).thenReturn(false);
        when(insumoRepository.save(any(Insumo.class))).thenReturn(insumoPrueba);

        Insumo resultado = insumoService.registrarInsumo(insumoPrueba);

        assertEquals("Hidrocortisona", resultado.getNombre());
        verify(insumoRepository, times(1)).save(insumoPrueba);
    }

    @Test
    void eliminarInsumo_DebeLanzarExcepcion_CuandoFormaParteDeUnaFormula() {
        when(insumoRepository.existsById(40L)).thenReturn(true);
        when(componenteRepository.existsByInsumoId(40L)).thenReturn(true);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            insumoService.eliminarInsumo(40L);
        });

        assertTrue(excepcion.getMessage().contains("forma parte de una formula"));
        verify(insumoRepository, never()).deleteById(anyLong());
    }

    @Test
    void listarBajoStock_DebeDevolverSoloLosQueEstanDebajoDelMinimo() {
        Insumo escaso = new Insumo();
        escaso.setId(41L);
        escaso.setNombre("Vaselina");
        escaso.setStock(new BigDecimal("5.000"));
        escaso.setStockMinimo(new BigDecimal("20.000"));
        when(insumoRepository.findAll()).thenReturn(List.of(insumoPrueba, escaso));

        List<Insumo> resultado = insumoService.listarBajoStock();

        assertEquals(1, resultado.size());
        assertEquals("Vaselina", resultado.get(0).getNombre());
    }
}
```

---

# Para subir

```bash
mvn test
git add .
git commit -m "..."
git push -u origin <tu-rama>
```

Y el Pull Request en GitHub: `base: main` <- `compare: <tu-rama>`.

**No toquen** `pom.xml`, `application.properties`, `ConsultorioMedicoApplication.java`, ni nada dentro de `model/` o `repository/`: ya estan listos y son compartidos.
