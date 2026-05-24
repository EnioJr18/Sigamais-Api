package br.edu.ifal.sigamais.service;


import lombok.RequiredArgsConstructor;
import br.edu.ifal.sigamais.model.Aluno;
import org.springframework.stereotype.Service;
import br.edu.ifal.sigamais.repository.AlunoRepository;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository repository;

    public Aluno salvar(Aluno aluno) {
        return repository.save(aluno);
    }
}
