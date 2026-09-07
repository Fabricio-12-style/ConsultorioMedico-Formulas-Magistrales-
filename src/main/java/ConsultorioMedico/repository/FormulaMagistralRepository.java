package ConsultorioMedico.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ConsultorioMedico.model.FormulaMagistral;

public interface FormulaMagistralRepository extends JpaRepository<FormulaMagistral, Long> {

    boolean existsByCodigo(String codigo);

    List<FormulaMagistral> findByActivoTrue();

    List<FormulaMagistral> findAllByOrderByNombreAsc();
}
