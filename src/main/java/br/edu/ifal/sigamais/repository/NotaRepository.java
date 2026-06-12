package br.edu.ifal.sigamais.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.ifal.sigamais.model.Nota;
import java.util.List;

public interface NotaRepository extends JpaRepository<Nota, Integer> {
    
    List<Nota> findByMatriculaId(Integer matriculaId);
}
