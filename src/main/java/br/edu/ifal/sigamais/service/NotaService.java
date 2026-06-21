package br.edu.ifal.sigamais.service;

import java.util.List;
import java.util.stream.Collectors;

import br.edu.ifal.sigamais.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import br.edu.ifal.sigamais.dto.NotaRequestDTO;
import br.edu.ifal.sigamais.dto.NotaResponseDTO;
import br.edu.ifal.sigamais.model.Nota;
import br.edu.ifal.sigamais.model.Matricula;
import br.edu.ifal.sigamais.repository.MatriculaRepository;
import br.edu.ifal.sigamais.repository.NotaRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class NotaService {
    
    private final NotaRepository notaRepository;
    private final MatriculaRepository matriculaRepository;

    public NotaResponseDTO cadastrarNota(NotaRequestDTO dto) {
        var matricula = matriculaRepository.findById(dto.matriculaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Matrícula não encontrada com o ID: " + dto.matriculaId()));

        Nota nota = new Nota();
        nota.setMatricula(matricula);
        nota.setValor(dto.valor());
        nota.setTipo(dto.tipo());

        Nota notaSalva = notaRepository.save(nota);

        return new NotaResponseDTO(notaSalva.getId(), notaSalva.getMatricula().getId(), notaSalva.getValor(), notaSalva.getTipo());
    }

    public List<NotaResponseDTO> listarNotasPorMatricula(Integer matriculaId) {
        List<Nota> notas = notaRepository.findByMatriculaId(matriculaId);

        return notas.stream()
            .map(nota -> new NotaResponseDTO(nota.getId(), nota.getMatricula().getId(), nota.getValor(), nota.getTipo()))
            .collect(Collectors.toList());
    }

    public boolean verificarAprovacaoPorMedia(Integer matriculaId) {
        List<Nota> notas = notaRepository.findByMatriculaId(matriculaId);

        if (notas.isEmpty()) {
            return false;
        }

        BigDecimal soma = BigDecimal.ZERO;
        for (Nota nota : notas) {
            soma = soma.add(nota.getValor());
        }

        BigDecimal media = soma.divide(BigDecimal.valueOf(notas.size()), 2, RoundingMode.HALF_UP);

        return media.compareTo(BigDecimal.valueOf(7.0)) >= 0;
    }

    public String calcularRiscoPorNota(Integer matriculaId) {
        List<Nota> notas = notaRepository.findByMatriculaId(matriculaId);

        if (notas.isEmpty()) {
            return "MEDIO";
        }

        BigDecimal soma = BigDecimal.ZERO;
        for (Nota nota : notas) {
            soma = soma.add(nota.getValor());
        }
        BigDecimal media = soma.divide(BigDecimal.valueOf(notas.size()), 2, RoundingMode.HALF_UP);

        if (media.compareTo(BigDecimal.valueOf(5.0)) < 0) {
            return "ALTO";
        }

        else if (media.compareTo(BigDecimal.valueOf(7.0)) < 0) {
            return "MEDIO";
        }

        else {
            return "BAIXO";
        }
    }

    public List<NotaResponseDTO> listarTodasNotas() {
        List<Nota> notas = notaRepository.findAll();

        return notas.stream()
            .map(nota -> new NotaResponseDTO(nota.getId(), nota.getMatricula().getId(), nota.getValor(), nota.getTipo()))
            .collect(Collectors.toList());
    }
}
