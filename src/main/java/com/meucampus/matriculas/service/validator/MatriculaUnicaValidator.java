package com.meucampus.matriculas.service.validator;

import com.meucampus.matriculas.domain.Matricula;
import com.meucampus.matriculas.domain.SituacaoMatricula;
import com.meucampus.matriculas.exception.RegraNegocioException;
import com.meucampus.matriculas.repository.MatriculaRepository;
import org.springframework.stereotype.Component;

@Component
public class MatriculaUnicaValidator {

    private final MatriculaRepository repository;

    public MatriculaUnicaValidator(MatriculaRepository matriculaRepository) {

        this.repository = matriculaRepository;

    }

    public void validarDuplicidade(Matricula matricula) {
        if (matricula == null || matricula.getAlunoId() == null || matricula.getTurmaId() == null) {
            return;
        }

        boolean jaExisteAtiva;

        if (matricula.getId() == null) {
            jaExisteAtiva = repository.existsByAlunoIdAndTurmaIdAndSituacao(
                    matricula.getAlunoId(),
                    matricula.getTurmaId(),
                    SituacaoMatricula.ATIVA);
        } else {
            jaExisteAtiva = repository.existsByAlunoIdAndTurmaIdAndSituacaoAndIdNot(
                    matricula.getAlunoId(),
                    matricula.getTurmaId(),
                    SituacaoMatricula.ATIVA,
                    matricula.getId());
        }

        if (jaExisteAtiva) {
            throw new RegraNegocioException("O aluno já possui uma matrícula ativa nesta turma.");
        }
    }
}