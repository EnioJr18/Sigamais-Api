package br.edu.ifal.sigamais.controller;

import br.edu.ifal.sigamais.dto.UsuarioResponseDTO;
import br.edu.ifal.sigamais.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        // Quebra o loop infinito e manda os dados limpos pro David
        var lista = usuarioRepository.findAll().stream()
                .map(u -> new UsuarioResponseDTO(u.getId(), u.getNome(), u.getEmail(), u.getPerfil()))
                .toList();

        return ResponseEntity.ok(lista);
    }
}