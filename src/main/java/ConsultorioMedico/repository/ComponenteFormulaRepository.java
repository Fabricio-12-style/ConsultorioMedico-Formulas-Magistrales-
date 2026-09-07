package ConsultorioMedico.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ConsultorioMedico.model.ComponenteFormula;

public interface ComponenteFormulaRepository extends JpaRepository<ComponenteFormula, Long> {

    List<ComponenteFormula> findByFormulaId(Long formulaId);

    boolean existsByFormulaIdAndInsumoId(Long formulaId, Long insumoId);

    boolean existsByInsumoId(Long insumoId);
}
