package com.meucampus.matriculas.service;

import com.meucampus.matriculas.domain.Matricula;
import com.meucampus.matriculas.domain.SituacaoMatricula;
import com.meucampus.matriculas.repository.MatriculaRepository;
import com.meucampus.matriculas.service.validator.MatriculaUnicaValidator;
import org.springframework.stereotype.Service;

@Service
public class CadastrarMatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final MatriculaUnicaValidator matriculaUnicaValidator;

    public CadastrarMatriculaService(MatriculaRepository matriculaRepository, MatriculaUnicaValidator matriculaUnicaValidator) {

        this.matriculaRepository = matriculaRepository;
        this.matriculaUnicaValidator = matriculaUnicaValidator;

    }

    public Matricula executar(Matricula matricula) {

        matricula.setSituacao(SituacaoMatricula.ATIVA);

        matriculaUnicaValidator.validarDuplicidade(matricula);

        return matriculaRepository.cadastrar(matricula);

    }
    
}