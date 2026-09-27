package com.meucampus.matriculas.service;

import com.meucampus.matriculas.domain.Matricula;
import com.meucampus.matriculas.domain.SituacaoMatricula;
import com.meucampus.matriculas.repository.MatriculaRepository;
import com.meucampus.matriculas.service.validator.MatriculaUnicaValidator;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AtualizarMatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final MatriculaUnicaValidator matriculaUnicaValidator;

    public AtualizarMatriculaService(MatriculaRepository matriculaRepository, MatriculaUnicaValidator matriculaUnicaValidator) {

        this.matriculaRepository = matriculaRepository;
        this.matriculaUnicaValidator = matriculaUnicaValidator;

    }

    public Optional<Matricula> executar(long id, Matricula matriculaAtualizada) {

        Optional<Matricula> existente = matriculaRepository.buscarPorId(id);

        if (existente.isEmpty()) {

            return Optional.empty();

        }

        matriculaAtualizada.setId(id);

        if (matriculaAtualizada.getSituacao() == SituacaoMatricula.ATIVA) {

            matriculaUnicaValidator.validarDuplicidade(matriculaAtualizada);

        }

        return matriculaRepository.atualizar(matriculaAtualizada);
        
    }
}