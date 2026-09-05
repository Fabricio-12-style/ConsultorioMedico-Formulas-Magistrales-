package ConsultorioMedico.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor


public class Paciente {

    private Long id;
    private String nombre;
    private String dni;
    private String telefono;
    private String email;
}
