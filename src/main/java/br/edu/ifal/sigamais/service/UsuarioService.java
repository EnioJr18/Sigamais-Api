package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.dto.*;
import br.edu.ifal.sigamais.exception.RecursoNaoEncontradoException;
import br.edu.ifal.sigamais.model.*;
import br.edu.ifal.sigamais.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AlunoRepository alunoRepository;
    private final MatriculaRepository matriculaRepository;
    private final NotaRepository notaRepository;
    private final FrequenciaRepository frequenciaRepository;
    private final AnaliseRiscoService analiseRiscoService;
    private final PasswordEncoder passwordEncoder;
    private final ProfessorRepository professorRepository;

    private Usuario getUsuarioLogado() {

        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = (Usuario) usuarioRepository.findByEmail(emailLogado);

        if (usuario == null) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado no sistema.");
        }

        return usuario;
    }

    public UsuarioPerfilDTO obterMeuPerfil() {
        Usuario usuario = getUsuarioLogado();
        boolean alertaVermelho = false;

        if ("ALUNO".equals(usuario.getPerfil())) {
            try {
                alertaVermelho = listarMeusRiscos().stream()
                        .anyMatch(map -> {
                            AlertaRiscoDTO riscoDTO = (AlertaRiscoDTO) map.get("risco");
                            return "ALTO".equals(riscoDTO.risco());
                        });
            } catch (Exception e) {
            }
        }

        return new UsuarioPerfilDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getCpf(),
                usuario.getEmail(),
                usuario.getPerfil(),
                usuario.getTelefone(),
                usuario.getEndereco(),
                usuario.getFotoPerfilUrl(),
                alertaVermelho
        );
    }

    public UsuarioPerfilDTO atualizarMeuPerfil(UsuarioPerfilAtualizacaoDTO dto) {
        Usuario usuario = getUsuarioLogado();

        // Atualiza apenas os campos permitidos
        usuario.setNome(dto.nome());
        usuario.setTelefone(dto.telefone());
        usuario.setEndereco(dto.endereco());
        usuario.setFotoPerfilUrl(dto.fotoPerfilUrl());

        usuarioRepository.save(usuario);

        // Reaproveita o método de cima para devolver o DTO atualizado
        return obterMeuPerfil();
    }

    // 1. Método Utilitário para achar o Aluno logado
    private Aluno getAlunoLogado() {
        Usuario usuario = getUsuarioLogado();

        // Truque de segurança: filtra todos os alunos buscando o que tem o mesmo ID do usuário logado
        return alunoRepository.findAll().stream()
                .filter(a -> a.getUsuario().getId().equals(usuario.getId()))
                .findFirst()
                .orElseThrow(() -> new RecursoNaoEncontradoException("Este usuário não possui um perfil de Aluno associado."));
    }

    // 2. Minhas Matrículas
    public List<MatriculaResponseDTO> listarMinhasMatriculas() {
        Aluno aluno = getAlunoLogado();
        return matriculaRepository.findAll().stream()
                .filter(m -> m.getAluno().getId().equals(aluno.getId()))
                .map(m -> new MatriculaResponseDTO(
                        m.getId(),
                        m.getAluno().getId(),
                        m.getAluno().getUsuario().getNome(),
                        m.getAluno().getMatricula(),
                        m.getTurma().getId(),
                        m.getTurma().getSemestre(),
                        m.getTurma().getAno(),
                        m.getTurma().getDisciplina().getNome(),
                        m.getTurma().getProfessor().getUsuario().getNome()
                ))
                .toList();
    }

    // 3. Minhas Notas
    public List<NotaResumoDTO> listarMinhasNotas() {
        Aluno aluno = getAlunoLogado();
        return matriculaRepository.findAll().stream()
                .filter(m -> m.getAluno().getId().equals(aluno.getId()))
                .map(m -> {
                    List<Nota> notas = notaRepository.findByMatriculaId(m.getId());
                    BigDecimal media = BigDecimal.ZERO;
                    if (!notas.isEmpty()) {
                        BigDecimal soma = BigDecimal.ZERO;
                        for (Nota n : notas) soma = soma.add(n.getValor());
                        media = soma.divide(BigDecimal.valueOf(notas.size()), 2, RoundingMode.HALF_UP);
                    }
                    String situacao = media.compareTo(new BigDecimal("7.0")) >= 0 ? "APROVADO" : (media.compareTo(new BigDecimal("5.0")) >= 0 ? "RECUPERACAO" : "REPROVADO");

                    return new NotaResumoDTO(
                            m.getId(), m.getAluno().getUsuario().getNome(), m.getAluno().getMatricula(),
                            m.getTurma().getDisciplina().getNome(), m.getTurma().getProfessor().getUsuario().getNome(),
                            m.getTurma().getSemestre(), media, notas.size(), situacao, notas.stream().map(n -> new NotaDetalheDTO(n.getId(), n.getTipo(), n.getValor())).toList()
                    );
                }).toList();
    }

    // 4. Minhas Frequências
    public List<FrequenciaResumoDTO> listarMinhasFrequencias() {
        Aluno aluno = getAlunoLogado();
        return matriculaRepository.findAll().stream()
                .filter(m -> m.getAluno().getId().equals(aluno.getId()))
                .map(m -> {
                    List<Frequencia> frequencias = frequenciaRepository.findByMatriculaId(m.getId());
                    int totalFaltas = frequencias.stream().mapToInt(Frequencia::getFaltas).sum();

                    return new FrequenciaResumoDTO(
                            m.getId(), m.getAluno().getUsuario().getNome(), m.getAluno().getMatricula(),
                            m.getTurma().getDisciplina().getNome(), m.getTurma().getProfessor().getUsuario().getNome(),
                            m.getTurma().getSemestre(), totalFaltas, frequencias.size()
                    );
                }).toList();
    }

    // 5. Meus Riscos (Como ele tem várias disciplinas, devolvemos uma lista customizada)
    public List<Map<String, Object>> listarMeusRiscos() {
        Aluno aluno = getAlunoLogado();
        return matriculaRepository.findAll().stream()
                .filter(m -> m.getAluno().getId().equals(aluno.getId()))
                .map(m -> {
                    AlertaRiscoDTO riscoDTO = analiseRiscoService.analisarRiscoMatricula(m.getId());
                    // Devolve o risco junto com o nome da disciplina para a tela fazer sentido
                    return Map.of(
                            "disciplina", m.getTurma().getDisciplina().getNome(),
                            "risco", riscoDTO
                    );
                }).toList();
    }

    public void alterarMinhaSenha(AlterarSenhaDTO dto) {
        Usuario usuario = getUsuarioLogado();

        // Verifica se a senha atual digitada bate com a senha criptografada do banco
        if (!passwordEncoder.matches(dto.senhaAtual(), usuario.getSenha())) {
            throw new IllegalArgumentException("A senha atual informada está incorreta.");
        }

        // Se bater, criptografa a nova senha e salva
        usuario.setSenha(passwordEncoder.encode(dto.novaSenha()));
        usuarioRepository.save(usuario);
    }

    private Professor getProfessorLogado() {
        Usuario usuario = getUsuarioLogado();
        return professorRepository.findAll().stream()
                .filter(p -> p.getUsuario().getId().equals(usuario.getId()))
                .findFirst()
                .orElseThrow(() -> new RecursoNaoEncontradoException("Este usuário não possui um perfil de Professor associado."));
    }

    public List<NotaResumoDTO> listarMinhasNotasProfessor() {
        Professor professor = getProfessorLogado();
        return matriculaRepository.findAll().stream()
                // O SEGREDO ESTÁ AQUI: Só passa se o professor da turma for o professor logado!
                .filter(m -> m.getTurma().getProfessor().getId().equals(professor.getId()))
                .map(m -> {
                    List<Nota> notas = notaRepository.findByMatriculaId(m.getId());

                    BigDecimal media = BigDecimal.ZERO;
                    if (!notas.isEmpty()) {
                        BigDecimal soma = BigDecimal.ZERO;
                        for (Nota n : notas) soma = soma.add(n.getValor());
                        media = soma.divide(BigDecimal.valueOf(notas.size()), 2, RoundingMode.HALF_UP);
                    }
                    String situacao = media.compareTo(new BigDecimal("7.0")) >= 0 ? "APROVADO" : (media.compareTo(new BigDecimal("5.0")) >= 0 ? "RECUPERACAO" : "REPROVADO");

                     List<NotaDetalheDTO> detalhes = notas.stream()
                            .map(n -> new NotaDetalheDTO(n.getId(), n.getTipo(), n.getValor()))
                            .toList();

                    return new NotaResumoDTO(
                            m.getId(), m.getAluno().getUsuario().getNome(), m.getAluno().getMatricula(),
                            m.getTurma().getDisciplina().getNome(), m.getTurma().getProfessor().getUsuario().getNome(),
                            m.getTurma().getSemestre(), media, notas.size(), situacao, detalhes
                    );
                }).toList();
    }

    public List<FrequenciaResumoDTO> listarMinhasFrequenciasProfessor() {
        Professor professor = getProfessorLogado();
        return matriculaRepository.findAll().stream()
                .filter(m -> m.getTurma().getProfessor().getId().equals(professor.getId()))
                .map(m -> {
                    List<Frequencia> frequencias = frequenciaRepository.findByMatriculaId(m.getId());
                    int totalFaltas = frequencias.stream().mapToInt(Frequencia::getFaltas).sum();

                    return new FrequenciaResumoDTO(
                            m.getId(), m.getAluno().getUsuario().getNome(), m.getAluno().getMatricula(),
                            m.getTurma().getDisciplina().getNome(), m.getTurma().getProfessor().getUsuario().getNome(),
                            m.getTurma().getSemestre(), totalFaltas, frequencias.size()
                    );
                }).toList();
    }

    public List<Map<String, Object>> listarMeusRiscosProfessor() {
        Professor professor = getProfessorLogado();

        return matriculaRepository.findAll().stream()
                .filter(m -> m.getTurma().getProfessor().getId().equals(professor.getId()))
                .map(m -> {
                    AlertaRiscoDTO riscoDTO = analiseRiscoService.analisarRiscoMatricula(m.getId());

                    return java.util.Map.of(
                            "matriculaId", m.getId(),
                            "alunoNome", m.getAluno().getUsuario().getNome(),
                            "alunoMatricula", m.getAluno().getMatricula(),
                            "disciplina", m.getTurma().getDisciplina().getNome(),
                            "semestre", m.getTurma().getSemestre(),
                            "risco", riscoDTO
                    );
                }).toList();
    }
}