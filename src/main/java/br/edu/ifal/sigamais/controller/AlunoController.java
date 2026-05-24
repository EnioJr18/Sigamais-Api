package br.edu.ifal.sigamais.controller;

import lombok.RequiredArgsConstructor;
import br.edu.ifal.sigamais.model.Aluno;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.edu.ifal.sigamais.service.AlunoService;

@RestController
@RequestMapping("/alunos")
@RequiredArgsConstructor
public class AlunoController {

    private final AlunoService service;

    @PostMapping
    public Aluno cadastrar(@RequestBody Aluno aluno) {
        return service.salvar(aluno);
    }
}
