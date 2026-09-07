package ConsultorioMedico.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ConsultorioMedico.model.Medico;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    boolean existsByCmp(String cmp);
}
