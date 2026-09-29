package com.meucampus.matriculas.service.validator;

import com.meucampus.matriculas.domain.Matricula;
import com.meucampus.matriculas.domain.SituacaoMatricula;
import com.meucampus.matriculas.exception.RegraNegocioException;
import com.meucampus.matriculas.repository.MatriculaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

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

        List<Matricula> todas = repository.listar();

        boolean jaExisteAtiva = todas.stream().anyMatch(m ->
                m.getAlunoId().equals(matricula.getAlunoId()) &&
                m.getTurmaId().equals(matricula.getTurmaId()) &&
                m.getSituacao() == SituacaoMatricula.ATIVA &&
                !m.getId().equals(matricula.getId())
        );

        if (jaExisteAtiva) {

            throw new RegraNegocioException("O aluno já possui uma matrícula ativa nesta turma.");

        }

    }

}