package br.edu.ifal.sigamais.service;

import java.util.List;
import java.util.stream.Collectors;

import br.edu.ifal.sigamais.dto.FrequenciaResumoDTO;
import br.edu.ifal.sigamais.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import br.edu.ifal.sigamais.dto.FrequenciaRequestDTO;
import br.edu.ifal.sigamais.dto.FrequenciaResponseDTO;
import br.edu.ifal.sigamais.model.Frequencia;
import br.edu.ifal.sigamais.model.Matricula;
import br.edu.ifal.sigamais.repository.FrequenciaRepository;
import br.edu.ifal.sigamais.repository.MatriculaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FrequenciaService {
    
    private final FrequenciaRepository frequenciaRepository;
    private final MatriculaRepository matriculaRepository;

    public FrequenciaResponseDTO registrarFrequencia(FrequenciaRequestDTO dto) {

        var matricula = matriculaRepository.findById(dto.matriculaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Matrícula não encontrada com o ID: " + dto.matriculaId()));

        Frequencia frequencia = new Frequencia();
        frequencia.setMatricula(matricula);
        frequencia.setFaltas(dto.faltas());

        Frequencia frequenciaSalva = frequenciaRepository.save(frequencia);

        return new FrequenciaResponseDTO(
            frequenciaSalva.getId(),
            frequenciaSalva.getMatricula().getId(),
            frequenciaSalva.getFaltas()
        );
    }

    public List<FrequenciaResponseDTO> listarFrequenciaPorMatricula(Integer matriculaId) {
        List<Frequencia> frequencias = frequenciaRepository.findByMatriculaId(matriculaId);

        return frequencias.stream()
            .map(f -> new FrequenciaResponseDTO(f.getId(), f.getMatricula().getId(), f.getFaltas()))
            .collect(Collectors.toList());
    }

    public boolean verificarReprovacaoPorFalta(Integer matriculaId) {
        List<Frequencia> frequencias = frequenciaRepository.findByMatriculaId(matriculaId);

        int totalFaltasAcumuladas = frequencias.stream()
            .mapToInt(Frequencia::getFaltas)
            .sum();
        
        Matricula matricula = matriculaRepository.findById(matriculaId)
            .orElseThrow(() -> new RuntimeException("Matrícula não encontrada!"));
        
        int cargaHorariaTurma = matricula.getTurma().getDisciplina().getCargaHoraria();

        double limiteFaltasPermitido = cargaHorariaTurma * 0.25;

        return totalFaltasAcumuladas > limiteFaltasPermitido;
    }

    public String calcularRiscoPorFalta(Integer matriculaId) {
        List<Frequencia> frequencias = frequenciaRepository.findByMatriculaId(matriculaId);

        int totalFaltasAcumuladas = frequencias.stream()
            .mapToInt(Frequencia::getFaltas)
            .sum();

        Matricula matricula = matriculaRepository.findById(matriculaId)
            .orElseThrow(() -> new RuntimeException("Matrícula não encontrada!"));

        int cargaHorariaTurma = matricula.getTurma().getDisciplina().getCargaHoraria();
        double limiteFaltasPermitido = cargaHorariaTurma * 0.25;

        if (limiteFaltasPermitido == 0) return "BAIXO";

        double proporcaoGasta = totalFaltasAcumuladas / limiteFaltasPermitido;

        if (proporcaoGasta > 0.8) {
            return "ALTO";
        }
        else if (proporcaoGasta >= 0.5) {
            return "MEDIO";
        }
        else {
            return "BAIXO";
        }
    }

    public List<FrequenciaResponseDTO> listarTodasFrequencias() {
        List<Frequencia> frequencias = frequenciaRepository.findAll();

        return frequencias.stream()
            .map(f -> new FrequenciaResponseDTO(f.getId(), f.getMatricula().getId(), f.getFaltas()))
            .collect(Collectors.toList());
    }

    public List<FrequenciaResumoDTO> listarResumoFrequencias() {
        return matriculaRepository.findAll().stream().map(m -> {
            List<Frequencia> frequencias = frequenciaRepository.findByMatriculaId(m.getId());

            int totalFaltas = frequencias.stream().mapToInt(Frequencia::getFaltas).sum();

            return new FrequenciaResumoDTO(
                    m.getId(),
                    m.getAluno().getUsuario().getNome(),
                    m.getAluno().getMatricula(),
                    m.getTurma().getDisciplina().getNome(),
                    m.getTurma().getProfessor().getUsuario().getNome(),
                    m.getTurma().getSemestre(),
                    totalFaltas,
                    frequencias.size()
            );
        }).toList();
    }
}
