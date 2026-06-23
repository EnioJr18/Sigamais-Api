package br.edu.ifal.sigamais.controller;

import br.edu.ifal.sigamais.dto.*;
import br.edu.ifal.sigamais.repository.UsuarioRepository;
import br.edu.ifal.sigamais.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        // Quebra o loop infinito e manda os dados limpos pro David
        var lista = usuarioRepository.findAll().stream()
                .map(u -> new UsuarioResponseDTO(u.getId(), u.getNome(), u.getEmail(), u.getPerfil()))
                .toList();

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioPerfilDTO> obterMeuPerfil() {
        return ResponseEntity.ok(usuarioService.obterMeuPerfil());
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioPerfilDTO> atualizarMeuPerfil(@RequestBody UsuarioPerfilAtualizacaoDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizarMeuPerfil(dto));
    }

    @GetMapping("/me/matriculas")
    public ResponseEntity<List<MatriculaResponseDTO>> listarMinhasMatriculas() {
        return ResponseEntity.ok(usuarioService.listarMinhasMatriculas());
    }

    @GetMapping("/me/notas")
    public ResponseEntity<List<NotaResumoDTO>> listarMinhasNotas() {
        return ResponseEntity.ok(usuarioService.listarMinhasNotas());
    }

    @GetMapping("/me/frequencias")
    public ResponseEntity<List<FrequenciaResumoDTO>> listarMinhasFrequencias() {
        return ResponseEntity.ok(usuarioService.listarMinhasFrequencias());
    }

    @GetMapping("/me/risco")
    public ResponseEntity<List<Map<String, Object>>> listarMeusRiscos() {
        return ResponseEntity.ok(usuarioService.listarMeusRiscos());
    }

    @PutMapping("/me/senha")
    public ResponseEntity<?> alterarMinhaSenha(@RequestBody AlterarSenhaDTO dto) {
        try {
            usuarioService.alterarMinhaSenha(dto);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/me/professor/notas")
    public ResponseEntity<List<NotaResumoDTO>> listarMinhasNotasProfessor() {
        return ResponseEntity.ok(usuarioService.listarMinhasNotasProfessor());
    }

    @GetMapping("/me/professor/frequencias")
    public ResponseEntity<List<FrequenciaResumoDTO>> listarMinhasFrequenciasProfessor() {
        return ResponseEntity.ok(usuarioService.listarMinhasFrequenciasProfessor());
    }

    @GetMapping("/me/professor/risco")
    public ResponseEntity<List<Map<String, Object>>> listarMeusRiscosProfessor() {
        return ResponseEntity.ok(usuarioService.listarMeusRiscosProfessor());
    }
}