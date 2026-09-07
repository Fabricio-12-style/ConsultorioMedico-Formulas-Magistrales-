package ConsultorioMedico.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "formulas_magistrales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FormulaMagistral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El codigo de la formula es obligatorio")
    @Size(max = 20, message = "El codigo no puede superar los 20 caracteres")
    @Column(unique = true, length = 20)
    private String codigo;

    @NotBlank(message = "El nombre de la formula es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @NotNull(message = "Debe indicar la forma farmaceutica")
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private FormaFarmaceutica formaFarmaceutica;

    @NotBlank(message = "Debe indicar la via de administracion")
    @Size(max = 30, message = "La via de administracion no puede superar los 30 caracteres")
    private String viaAdministracion;

    @Size(max = 500, message = "La descripcion no puede superar los 500 caracteres")
    @Column(length = 500)
    private String descripcion;

    @Size(max = 500, message = "Las indicaciones no pueden superar los 500 caracteres")
    @Column(length = 500)
    private String indicaciones;

    private Boolean activo = true;
}
