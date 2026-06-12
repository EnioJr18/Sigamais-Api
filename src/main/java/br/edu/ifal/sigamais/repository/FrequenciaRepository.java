package br.edu.ifal.sigamais.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.ifal.sigamais.model.Frequencia;
import java.util.List;

public interface FrequenciaRepository extends JpaRepository<Frequencia, Long> {

    List<Frequencia> findByMatriculaId(Long matriculaId);
}