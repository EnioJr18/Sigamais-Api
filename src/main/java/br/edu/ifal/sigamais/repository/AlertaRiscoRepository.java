package br.edu.ifal.sigamais.repository;

import br.edu.ifal.sigamais.model.AlertaRisco;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AlertaRiscoRepository extends JpaRepository<AlertaRisco, Integer> {
    Optional<AlertaRisco> findByMatriculaId(Integer matriculaId);
}