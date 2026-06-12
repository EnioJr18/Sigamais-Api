package br.edu.ifal.sigamais.repository;

import br.edu.ifal.sigamais.model.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Integer> {

    long countByTurmaId(Integer turmaId);

    boolean existsByAlunoIdAndTurmaDisciplinaIdAndStatus(Integer alunoId, Integer disciplinaId, String status);

}