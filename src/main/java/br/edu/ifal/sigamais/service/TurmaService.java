package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.dto.TurmaRequestDTO;
import br.edu.ifal.sigamais.dto.TurmaResponseDTO;
import br.edu.ifal.sigamais.exception.RecursoNaoEncontradoException;
import br.edu.ifal.sigamais.model.Turma;
import br.edu.ifal.sigamais.repository.DisciplinaRepository;
import br.edu.ifal.sigamais.repository.ProfessorRepository;
import br.edu.ifal.sigamais.repository.TurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TurmaService {

    private final TurmaRepository turmaRepo;
    private final ProfessorRepository profRepo;
    private final DisciplinaRepository discRepo;

    public Turma salvar(TurmaRequestDTO dto) {

        // 2. Agora você extrai os IDs de dentro do DTO para fazer as buscas
        var professor = profRepo.findById(dto.professorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Professor não encontrado com o ID: " + dto.professorId()));

        var disciplina = discRepo.findById(dto.disciplinaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Disciplina não encontrada com o ID: " + dto.disciplinaId()));

        // 3. Cria a Turma
        Turma turma = new Turma();
        turma.setProfessor(professor);
        turma.setDisciplina(disciplina);
        turma.setSemestre(dto.semestre());
        turma.setAno(dto.ano());

        turma.setVagas(dto.vagas());

        return turmaRepo.save(turma);
    }

    public List<TurmaResponseDTO> listarTodas() {
        return turmaRepo.findAll().stream()
                .map(t -> new TurmaResponseDTO(
                        t.getId(),
                        t.getSemestre(),
                        t.getAno(),
                        t.getVagas(),
                        t.getProfessor().getId(),
                        t.getProfessor().getUsuario().getNome(),
                        t.getDisciplina().getId(),
                        t.getDisciplina().getNome()
                ))
                .toList();
    }
}