package br.edu.ifal.sigamais.controller;

import org.springframework.web.bind.annotation.RestController;

import br.edu.ifal.sigamais.dto.NotaRequestDTO;
import br.edu.ifal.sigamais.dto.NotaResponseDTO;
import br.edu.ifal.sigamais.service.NotaService;

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
@RequestMapping("/notas")
@RequiredArgsConstructor
public class NotaController {
    
    private final NotaService notaService;

    @PostMapping
    public ResponseEntity<NotaResponseDTO> cadastrar(@RequestBody NotaRequestDTO dto) {
        NotaResponseDTO novaNota = notaService.cadastrarNota(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaNota);
    }

    @GetMapping("/matriculas/{matriculaId}")
    public ResponseEntity<List<NotaResponseDTO>> listarPorMatricula(@PathVariable Long matriculaId) {
        List<NotaResponseDTO> notas = notaService.listarNotasPorMatricula(matriculaId);
        return ResponseEntity.ok(notas);
    }


    @GetMapping("/matriculas/{matriculaId}/status-media")
    public ResponseEntity<Boolean> verificarAprovacaoMedia(@PathVariable Long matriculaId) {
        boolean aprovado = notaService.verificarAprovacaoPorMedia(matriculaId);
        return ResponseEntity.ok(aprovado);
    }


}