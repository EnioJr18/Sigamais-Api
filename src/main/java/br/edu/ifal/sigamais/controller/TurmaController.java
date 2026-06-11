package br.edu.ifal.sigamais.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifal.sigamais.model.Turma;
import br.edu.ifal.sigamais.service.TurmaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/turmas")
@RequiredArgsConstructor
public class TurmaController {

    private final TurmaService turmaService;

    @PostMapping
    public ResponseEntity<Turma> criarTurma(
            @RequestParam Integer professorId,
            @RequestParam Integer disciplinaId,
            @RequestParam String semestre,
            @RequestParam Integer ano) {
        
        Turma novaTurma = turmaService.salvar(professorId, disciplinaId, semestre, ano);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaTurma);
    }
}