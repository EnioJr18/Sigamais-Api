package br.edu.ifal.sigamais.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifal.sigamais.model.Disciplina;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Integer> {
}