package br.edu.ifal.sigamais.service;

import java.util.List;
import java.util.stream.Collectors;

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
        Matricula matricula = matriculaRepository.findById(dto.matriculaId())
            .orElseThrow(() -> new RuntimeException("Matrícula não encontrada!"));
        
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
}
