package ConsultorioMedico.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor


public class CitaMedica {
    private Long id;
    private Long pacienteId;
    private Long medicoId;
    private LocalDateTime fechaHora;
    private String motivo;
    private EstadoCita estado = EstadoCita.PENDIENTE;
    
}
