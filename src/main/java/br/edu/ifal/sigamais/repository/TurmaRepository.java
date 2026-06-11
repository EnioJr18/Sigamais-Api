package br.edu.ifal.sigamais.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifal.sigamais.model.Turma;

public interface TurmaRepository extends JpaRepository<Turma, Integer> {
}