package ConsultorioMedico.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "componentes_formula", uniqueConstraints = @UniqueConstraint(name = "uk_formula_insumo", columnNames = {"formula_id", "insumo_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComponenteFormula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Debe indicar la formula a la que pertenece el componente")
    @Column(name = "formula_id")
    private Long formulaId;

    @NotNull(message = "Debe indicar el insumo del componente")
    @Column(name = "insumo_id")
    private Long insumoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    @Column(precision = 12, scale = 3)
    private BigDecimal cantidad;

    @NotBlank(message = "Debe indicar la unidad de medida")
    @Size(max = 10, message = "La unidad de medida no puede superar los 10 caracteres")
    @Column(length = 10)
    private String unidadMedida;
}
