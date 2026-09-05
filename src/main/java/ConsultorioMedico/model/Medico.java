package ConsultorioMedico.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Medico {
    private Long id;
    private String nombre;
    private String cmp;
    private String especialidad;
}
