package br.edu.ifal.sigamais.repository;

// Estas duas linhas são as que geralmente faltam e deixam tudo vermelho!
import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifal.sigamais.model.Professor;

public interface ProfessorRepository extends JpaRepository<Professor, Integer> {
}