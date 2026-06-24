package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.dto.MatriculaRequestDTO;
import br.edu.ifal.sigamais.dto.MatriculaResponseDTO;
import br.edu.ifal.sigamais.exception.LimitesVagasException;
import br.edu.ifal.sigamais.exception.PreRequisitoNaoAtendidoException;
import br.edu.ifal.sigamais.exception.RecursoNaoEncontradoException;
import br.edu.ifal.sigamais.model.Aluno;
import br.edu.ifal.sigamais.model.Matricula;
import br.edu.ifal.sigamais.model.Turma;
import br.edu.ifal.sigamais.repository.AlunoRepository;
import br.edu.ifal.sigamais.repository.MatriculaRepository;
import br.edu.ifal.sigamais.repository.TurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatriculaService {

    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;
    private final MatriculaRepository matriculaRepository;

    public MatriculaResponseDTO realizarMatricula(MatriculaRequestDTO request) {

        Aluno aluno = alunoRepository.findById(request.alunoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado."));

        Turma turma = turmaRepository.findById(request.turmaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Turma não encontrada."));

        // Validação da RN04: Limite de Vagas
        long matriculasAtuais = matriculaRepository.countByTurmaId(turma.getId());
        if (matriculasAtuais >= turma.getVagas()) {
            throw new LimitesVagasException("A turma selecionada não possui vagas disponíveis.");
        }

        // Validação da RN05: Pré-requisitos (Se a disciplina exigir pré-requisito)
        if (turma.getDisciplina().getPreRequisito() != null) {
            boolean jaCursou = matriculaRepository.existsByAlunoIdAndTurmaDisciplinaIdAndStatus(
                    aluno.getId(), turma.getDisciplina().getPreRequisito().getId(), "APROVADO");

            if (!jaCursou) {
                throw new PreRequisitoNaoAtendidoException("O aluno não possui o pré-requisito necessário para esta disciplina.");
            }
        }

        Matricula matricula = new Matricula();
        matricula.setAluno(aluno);
        matricula.setTurma(turma);
        matricula.setStatus("ATIVA");

        Matricula matriculaSalva = matriculaRepository.save(matricula);

        return new MatriculaResponseDTO(
                matriculaSalva.getId(),
                aluno.getId(),
                aluno.getUsuario().getNome(),
                aluno.getMatricula(),
                turma.getId(),
                turma.getSemestre(),
                turma.getAno(),
                turma.getDisciplina().getNome(),
                turma.getProfessor().getUsuario().getNome()
        );
    }

    public List<MatriculaResponseDTO> listarMatriculas() {
        return matriculaRepository.findAll().stream()
                .map(m -> new MatriculaResponseDTO(
                        m.getId(),
                        m.getAluno().getId(),
                        m.getAluno().getUsuario().getNome(),
                        m.getAluno().getMatricula(),
                        m.getTurma().getId(),
                        m.getTurma().getSemestre(),
                        m.getTurma().getAno(),
                        m.getTurma().getDisciplina().getNome(),
                        m.getTurma().getProfessor().getUsuario().getNome()
                ))
                .toList();
    }

    public Matricula atualizar(Integer id, MatriculaRequestDTO dto) {
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Matrícula não encontrada."));

        Turma novaTurma = turmaRepository.findById(dto.turmaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Turma não encontrada."));

        matricula.setTurma(novaTurma);

        return matriculaRepository.save(matricula);
    }

    public void deletar(Integer id) {
        if (!matriculaRepository.existsById(id)) {
            throw new IllegalArgumentException("Matrícula não encontrada.");
        }
        try {
            matriculaRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Não é possível excluir esta matrícula.");
        }
    }
}