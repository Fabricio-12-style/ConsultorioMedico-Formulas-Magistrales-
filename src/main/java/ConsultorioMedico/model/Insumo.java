package ConsultorioMedico.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

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
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "insumos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El codigo del insumo es obligatorio")
    @Size(max = 20, message = "El codigo no puede superar los 20 caracteres")
    @Column(unique = true, length = 20)
    private String codigo;

    @NotBlank(message = "El nombre del insumo es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @NotNull(message = "Debe indicar el tipo de insumo")
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoInsumo tipo;

    @NotBlank(message = "Debe indicar la unidad de medida")
    @Size(max = 10, message = "La unidad de medida no puede superar los 10 caracteres")
    @Column(length = 10)
    private String unidadMedida;

    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    @Column(precision = 12, scale = 3)
    private BigDecimal stock = BigDecimal.ZERO;

    @NotNull(message = "El stock minimo es obligatorio")
    @PositiveOrZero(message = "El stock minimo no puede ser negativo")
    @Column(precision = 12, scale = 3)
    private BigDecimal stockMinimo = BigDecimal.ZERO;

    @NotNull(message = "El precio unitario es obligatorio")
    @PositiveOrZero(message = "El precio unitario no puede ser negativo")
    @Column(precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @NotBlank(message = "El numero de lote es obligatorio")
    @Size(max = 30, message = "El lote no puede superar los 30 caracteres")
    @Column(length = 30)
    private String lote;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaVencimiento;
}
