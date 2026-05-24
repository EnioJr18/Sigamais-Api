package br.edu.ifal.sigamais.repository;

import br.edu.ifal.sigamais.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

}
