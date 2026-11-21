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

@Service
public class CadastrarMatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final MatriculaUnicaValidator matriculaUnicaValidator;
    private final CadastroClient cadastroClient;
    private final TurmasClient turmasClient;

    public CadastrarMatriculaService(
            MatriculaRepository matriculaRepository,
            MatriculaUnicaValidator matriculaUnicaValidator,
            CadastroClient cadastroClient,
            TurmasClient turmasClient) {
        this.matriculaRepository = matriculaRepository;
        this.matriculaUnicaValidator = matriculaUnicaValidator;
        this.cadastroClient = cadastroClient;
        this.turmasClient = turmasClient;
    }

    public Matricula executar(Matricula matricula) {

        if (matricula.getSituacao() != null && matricula.getSituacao() != SituacaoMatricula.ATIVA) {
            throw new IllegalArgumentException("Uma matrícula não pode ser cadastrada como trancada.");
        }

        matricula.setSituacao(SituacaoMatricula.ATIVA);

        cadastroClient.buscarAluno(matricula.getAlunoId());
        TurmaDTO turma = turmasClient.buscarTurma(matricula.getTurmaId());

        matriculaUnicaValidator.validarDuplicidade(matricula);

        if (turma.getVagas() == null) {
            throw new ServicoIndisponivelException(
                    "O serviço de Turmas não informou o número de vagas da turma.");
        }

        long matriculasAtivas = matriculaRepository.countByTurmaIdAndSituacao(
                matricula.getTurmaId(),
                SituacaoMatricula.ATIVA);

        if (matriculasAtivas >= turma.getVagas()) {
            throw new RegraNegocioException("A turma já está com todas as vagas ocupadas.");
        }

        return matriculaRepository.save(matricula);
    }
    
}