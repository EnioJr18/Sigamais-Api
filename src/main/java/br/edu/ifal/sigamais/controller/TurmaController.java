package br.edu.ifal.sigamais.controller;

import br.edu.ifal.sigamais.repository.TurmaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.edu.ifal.sigamais.model.Turma;
import br.edu.ifal.sigamais.service.TurmaService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/turmas")
@RequiredArgsConstructor
public class TurmaController {

    private final TurmaService turmaService;

    private final TurmaRepository turmaRepository;

    @PostMapping
    public ResponseEntity<Turma> criarTurma(
            @RequestParam Integer professorId,
            @RequestParam Integer disciplinaId,
            @RequestParam String semestre,
            @RequestParam Integer ano) {
        
        Turma novaTurma = turmaService.salvar(professorId, disciplinaId, semestre, ano);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaTurma);
    }

    @GetMapping
    public ResponseEntity<List<Turma>> listarTodas() {
        return ResponseEntity.ok(turmaRepository.findAll());
    }
}