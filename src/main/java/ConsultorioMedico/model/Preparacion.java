package ConsultorioMedico.model;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "preparaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Preparacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long citaId;

    @NotNull(message = "Debe indicar el paciente")
    private Long pacienteId;

    @NotNull(message = "Debe indicar el medico que receta")
    private Long medicoId;

    @NotNull(message = "Debe indicar la formula a preparar")
    private Long formulaId;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer cantidad;
    
    private LocalDateTime fechaSolicitud;

    @Future(message = "La fecha de entrega no puede estar en el pasado")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaEntrega;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EstadoPreparacion estado = EstadoPreparacion.PENDIENTE;

    @Size(max = 500, message = "Las observaciones no pueden superar los 500 caracteres")
    @Column(length = 500)
    private String observaciones;
}
