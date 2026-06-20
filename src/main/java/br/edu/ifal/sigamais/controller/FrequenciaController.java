package br.edu.ifal.sigamais.controller;

import org.springframework.web.bind.annotation.RestController;

import br.edu.ifal.sigamais.dto.FrequenciaRequestDTO;
import br.edu.ifal.sigamais.dto.FrequenciaResponseDTO;
import br.edu.ifal.sigamais.service.FrequenciaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/frequencias")
@RequiredArgsConstructor
public class FrequenciaController {
    
    private final FrequenciaService frequenciaService;

    @PostMapping
    public ResponseEntity<FrequenciaResponseDTO> registrar(@RequestBody FrequenciaRequestDTO dto) {
        FrequenciaResponseDTO novaFrequencia = frequenciaService.registrarFrequencia(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaFrequencia);
    }

    @GetMapping("/matriculas/{matriculaId}")
    public ResponseEntity<List<FrequenciaResponseDTO>> listarporMatricula(@PathVariable Integer matriculaId) {
        List<FrequenciaResponseDTO> frequencias = frequenciaService.listarFrequenciaPorMatricula(matriculaId);
        return ResponseEntity.ok(frequencias);
    }

    @GetMapping("/matriculas/{matriculaId}/status-reprovacao")
    public ResponseEntity<Boolean> verificarReprovacaoFalta(@PathVariable Integer matriculaId) {
        boolean reprovado = frequenciaService.verificarReprovacaoPorFalta(matriculaId);
        return ResponseEntity.ok(reprovado);
    }

    @GetMapping
    public ResponseEntity<List<FrequenciaResponseDTO>> listarTodos() {
        return ResponseEntity.ok(frequenciaService.listarTodasFrequencias());
    }
}
