package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.dto.MatriculaRequestDTO;
import br.edu.ifal.sigamais.dto.MatriculaResponseDTO;
import br.edu.ifal.sigamais.exception.LimitesVagasException;
import br.edu.ifal.sigamais.exception.PreRequisitoNaoAtendidoException;
import br.edu.ifal.sigamais.repository.AlunoRepository;
// import br.edu.ifal.sigamais.repository.TurmaRepository; // Descomentar quando o David entregar
// import br.edu.ifal.sigamais.repository.MatriculaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatriculaService {

    private final AlunoRepository alunoRepository;
    // private final TurmaRepository turmaRepository;
    // private final MatriculaRepository matriculaRepository;

    public MatriculaResponseDTO realizarMatricula(MatriculaRequestDTO request) {

        // 1. Aqui faremos a busca do Aluno e da Turma no banco

        // 2. Validação da RN04: Limite de Vagas
        /*
        if (turma.getMatriculasAtuais() >= turma.getVagas()) {
            throw new LimitesVagasException("A turma selecionada não possui vagas disponíveis.");
        }
        */

        // 3. Validação da RN05: Pré-requisitos
        /*
        if (!alunoTemPreRequisito(aluno, turma.getDisciplina())) {
            throw new PreRequisitoNaoAtendidoException("O aluno não possui o pré-requisito necessário para esta disciplina.");
        }
        */

        // 4. Salvar a Matrícula e retornar o DTO de sucesso

        return null; // Placeholder temporário
    }
}