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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "citas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CitaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Debe indicar el paciente de la cita")
    private Long pacienteId;

    @NotNull(message = "Debe indicar el medico que atendera la cita")
    private Long medicoId;

    @NotNull(message = "La fecha y hora son obligatorias")
    @Future(message = "No se pueden programar citas en el pasado")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaHora;

    @NotBlank(message = "El motivo no puede estar vacio")
    @Size(min = 3, max = 200, message = "El motivo debe tener entre 3 y 200 caracteres")
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EstadoCita estado = EstadoCita.PENDIENTE;
}
