package com.meucampus.matriculas.service;

import com.meucampus.matriculas.client.CadastroClient;
import com.meucampus.matriculas.client.TurmasClient;
import com.meucampus.matriculas.domain.Matricula;
import com.meucampus.matriculas.domain.SituacaoMatricula;
import com.meucampus.matriculas.dto.TurmaDTO;
import com.meucampus.matriculas.exception.RegraNegocioException;
import com.meucampus.matriculas.exception.ServicoIndisponivelException;
import com.meucampus.matriculas.repository.MatriculaRepository;
import com.meucampus.matriculas.service.validator.MatriculaUnicaValidator;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public Optional<Matricula> executar(long id, Matricula matriculaAtualizada) {

        Optional<Matricula> existente = matriculaRepository.findById(id);

        if (existente.isEmpty()) {
            return Optional.empty();
        }

        matriculaAtualizada.setId(id);

        if (matriculaAtualizada.getSituacao() == SituacaoMatricula.ATIVA) {
            cadastroClient.buscarAluno(matriculaAtualizada.getAlunoId());
            TurmaDTO turma = turmasClient.buscarTurma(matriculaAtualizada.getTurmaId());
            matriculaUnicaValidator.validarDuplicidade(matriculaAtualizada);

            boolean estaSendoReativada =
                    existente.get().getSituacao() == SituacaoMatricula.TRANCADA;

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

        return Optional.of(matriculaRepository.save(matriculaAtualizada));
        
    }

}