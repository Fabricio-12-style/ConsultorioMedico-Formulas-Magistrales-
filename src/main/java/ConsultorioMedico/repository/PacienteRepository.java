package ConsultorioMedico.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ConsultorioMedico.model.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
}
