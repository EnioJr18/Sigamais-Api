package br.edu.ifal.sigamais.service;

import br.edu.ifal.sigamais.dto.AlertaRiscoDTO;
import br.edu.ifal.sigamais.dto.HistoricoAlertaDTO;
import br.edu.ifal.sigamais.model.AlertaRisco;
import br.edu.ifal.sigamais.model.HistoricoAlertaRisco;
import br.edu.ifal.sigamais.model.Matricula;
import br.edu.ifal.sigamais.repository.AlertaRiscoRepository;
import br.edu.ifal.sigamais.repository.HistoricoAlertaRiscoRepository;
import br.edu.ifal.sigamais.repository.MatriculaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertaRiscoService {

    private final AlertaRiscoRepository alertaRepository;
    private final MatriculaRepository matriculaRepository;
    private final AnaliseRiscoService analiseRiscoService;
    private final EmailService emailService;
    private final HistoricoAlertaRiscoRepository historicoRepository;

    public void notificarCoordenacao(Integer matriculaId) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new IllegalArgumentException("Matrícula não encontrada."));

        // 1. Calcula o risco atual na hora
        AlertaRiscoDTO riscoDTO = analiseRiscoService.analisarRiscoMatricula(matriculaId);

        if (!riscoDTO.risco().equals("ALTO")) {
            throw new IllegalArgumentException("O aluno não está em Risco Alto no momento.");
        }

        // 2. Verifica se já existe um alerta para evitar Spam
        AlertaRisco alerta = alertaRepository.findByMatriculaId(matriculaId).orElse(new AlertaRisco());

        if (alerta.isEmailEnviado()) {
            throw new IllegalArgumentException("A coordenação já foi notificada sobre este aluno.");
        }

        // 3. Monta e salva o registro no banco
        alerta.setMatricula(matricula);
        alerta.setRisco(riscoDTO.risco());
        alerta.setMedia(riscoDTO.media());
        alerta.setFaltas(riscoDTO.faltas());
        alerta.setMotivos(String.join("\n- ", riscoDTO.motivos()));
        alerta.setEmailEnviado(true);
        alerta.setAtualizadoEm(LocalDateTime.now());
        alertaRepository.save(alerta);

        // Cria o registro inicial do histórico
        HistoricoAlertaRisco historicoInicial = new HistoricoAlertaRisco();
        historicoInicial.setAlertaRisco(alerta);
        historicoInicial.setStatus(alerta.getStatus());
        historicoInicial.setObservacao("Alerta criado automaticamente após risco alto.");
        historicoInicial.setCriadoEm(LocalDateTime.now());
        historicoInicial.setResponsavelNome("Sistema"); // Responsável automático
        historicoRepository.save(historicoInicial);

        // 4. Monta o texto do E-mail e dispara
        String nomeAluno = matricula.getAluno().getUsuario().getNome();
        String disciplina = matricula.getTurma().getDisciplina().getNome();

        // E-mail da coordenação hardcoded
        String emailCoordenacao = "coordenacao.if@gmail.com";

        String assunto = "🚨 URGENTE: Alerta de Evasão Acadêmica - " + nomeAluno;
        String texto = String.format(
                "Olá Coordenação,\n\n" +
                        "O aluno %s atingiu risco ALTO na disciplina de %s.\n\n" +
                        "📊 Resumo do Desempenho:\n" +
                        "Média Atual: %s\n" +
                        "Faltas Acumuladas: %d\n\n" +
                        "⚠️ Motivos Detectados:\n- %s\n\n" +
                        "Recomenda-se acompanhamento pedagógico imediato.\n\n" +
                        "Atenciosamente,\nSistema SIGA+",
                nomeAluno, disciplina, riscoDTO.media(), riscoDTO.faltas(), alerta.getMotivos()
        );

        emailService.enviarEmailAlerta(emailCoordenacao, assunto, texto);
    }

    public java.util.List<br.edu.ifal.sigamais.dto.AlertaResponseDTO> listarTodosAlertas() {
        return alertaRepository.findAll().stream()
                .map(alerta -> new br.edu.ifal.sigamais.dto.AlertaResponseDTO(
                        alerta.getId(),
                        alerta.getMatricula().getAluno().getUsuario().getNome(),
                        alerta.getMatricula().getAluno().getMatricula(),
                        alerta.getMatricula().getTurma().getDisciplina().getNome(),
                        alerta.getRisco(),
                        alerta.getMedia(),
                        alerta.getFaltas(),
                        alerta.getMotivos(),
                        alerta.getStatus(),
                        alerta.getObservacao(),
                        alerta.getCriadoEm()
                )).toList();
    }

    public void atualizarAlerta(Integer alertaId, br.edu.ifal.sigamais.dto.AtualizarAlertaDTO dto) {
        AlertaRisco alerta = alertaRepository.findById(alertaId)
                .orElseThrow(() -> new IllegalArgumentException("Alerta não encontrado."));

        alerta.setStatus(dto.status());
        alerta.setObservacao(dto.observacao());
        alerta.setAtualizadoEm(LocalDateTime.now());

        alertaRepository.save(alerta);

        // Adiciona a nova intervenção na linha do tempo
        HistoricoAlertaRisco novoHistorico = new HistoricoAlertaRisco();
        novoHistorico.setAlertaRisco(alerta);
        novoHistorico.setStatus(dto.status());
        novoHistorico.setObservacao(dto.observacao());
        novoHistorico.setCriadoEm(LocalDateTime.now());

        // Se quiser, no futuro pode puxar o nome do SecurityContext,
        novoHistorico.setResponsavelNome("Coordenação Siga+");

        historicoRepository.save(novoHistorico);
    }

    public List<HistoricoAlertaDTO> listarHistorico(Integer alertaId) {
        return historicoRepository.findByAlertaRiscoIdOrderByCriadoEmAsc(alertaId).stream()
                .map(h -> new HistoricoAlertaDTO(
                        h.getId(),
                        h.getStatus().name(),
                        h.getObservacao(),
                        h.getCriadoEm(),
                        h.getResponsavelNome()
                )).toList();
    }
}