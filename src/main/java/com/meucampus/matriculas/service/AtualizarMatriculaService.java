package com.meucampus.matriculas.service;

import com.meucampus.matriculas.client.CadastroClient;
import com.meucampus.matriculas.client.TurmasClient;
import com.meucampus.matriculas.domain.Matricula;
import com.meucampus.matriculas.domain.SituacaoMatricula;
import com.meucampus.matriculas.dto.TurmaDTO;
import com.meucampus.matriculas.exception.RegraNegocioException;
import com.meucampus.matriculas.exception.RecursoNaoEncontradoException;
import com.meucampus.matriculas.exception.ServicoIndisponivelException;
import com.meucampus.matriculas.repository.MatriculaRepository;
import com.meucampus.matriculas.service.validator.MatriculaUnicaValidator;
import org.springframework.stereotype.Service;

@Service
public class AtualizarMatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final MatriculaUnicaValidator matriculaUnicaValidator;
    private final CadastroClient cadastroClient;
    private final TurmasClient turmasClient;

    public AtualizarMatriculaService(
            MatriculaRepository matriculaRepository,
            MatriculaUnicaValidator matriculaUnicaValidator,
            CadastroClient cadastroClient,
            TurmasClient turmasClient) {
        this.matriculaRepository = matriculaRepository;
        this.matriculaUnicaValidator = matriculaUnicaValidator;
        this.cadastroClient = cadastroClient;
        this.turmasClient = turmasClient;
    }

    public Matricula executar(long id, Matricula matriculaAtualizada) {

        Matricula existente = matriculaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Matrícula com ID " + id + " não foi encontrada."));

        matriculaAtualizada.setId(id);

        if (matriculaAtualizada.getSituacao() == SituacaoMatricula.ATIVA) {
            cadastroClient.buscarAluno(matriculaAtualizada.getAlunoId());
            TurmaDTO turma = turmasClient.buscarTurma(matriculaAtualizada.getTurmaId());
            matriculaUnicaValidator.validarDuplicidade(matriculaAtualizada);

            boolean estaSendoReativada =
                    existente.getSituacao() == SituacaoMatricula.TRANCADA;

            if (estaSendoReativada) {
                if (turma.getVagas() == null) {
                    throw new ServicoIndisponivelException(
                            "O serviço de Turmas não informou o número de vagas da turma.");
                }

                long matriculasAtivas = matriculaRepository.countByTurmaIdAndSituacao(
                        matriculaAtualizada.getTurmaId(),
                        SituacaoMatricula.ATIVA);

                if (matriculasAtivas >= turma.getVagas()) {
                    throw new RegraNegocioException(
                            "A turma já está com todas as vagas ocupadas.");
                }
            }
        }

        return matriculaRepository.save(matriculaAtualizada);
        
    }

}