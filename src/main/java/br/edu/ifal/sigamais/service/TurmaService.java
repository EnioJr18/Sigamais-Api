package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.exception.RecursoNaoEncontradoException;
import br.edu.ifal.sigamais.model.Turma;
import br.edu.ifal.sigamais.repository.DisciplinaRepository;
import br.edu.ifal.sigamais.repository.ProfessorRepository;
import br.edu.ifal.sigamais.repository.TurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TurmaService {

    private final TurmaRepository turmaRepo;
    private final ProfessorRepository profRepo;
    private final DisciplinaRepository discRepo;

    public Turma salvar(Integer professorId, Integer disciplinaId, String semestre, Integer ano) {
        
        // 1. Verifica se o Professor existe
        var professor = profRepo.findById(professorId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Professor não encontrado com o ID: " + professorId));
        
        // 2. Verifica se a Disciplina existe
        var disciplina = discRepo.findById(disciplinaId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Disciplina não encontrada com o ID: " + disciplinaId));

        // 3. Cria a Turma
        Turma turma = new Turma();
        turma.setProfessor(professor);
        turma.setDisciplina(disciplina);
        turma.setSemestre(semestre);
        turma.setAno(ano);
        
        // 4. Salva no banco
        return turmaRepo.save(turma);
    }
}