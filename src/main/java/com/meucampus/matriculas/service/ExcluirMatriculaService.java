package com.meucampus.matriculas.service;

import com.meucampus.matriculas.repository.MatriculaRepository;
import org.springframework.stereotype.Service;
import com.meucampus.matriculas.domain.Matricula;
import com.meucampus.matriculas.domain.SituacaoMatricula;
import com.meucampus.matriculas.exception.RegraNegocioException;
import com.meucampus.matriculas.exception.RecursoNaoEncontradoException;

@Service
public class ExcluirMatriculaService {

    private final MatriculaRepository matriculaRepository;

    public ExcluirMatriculaService(MatriculaRepository matriculaRepository) {

        this.matriculaRepository = matriculaRepository;
        
    }

    public void executar(long id) {

        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "A matrícula " + id + " não foi encontrada."));

        if (matricula.getSituacao() != SituacaoMatricula.TRANCADA) {
            throw new RegraNegocioException("Só é possível excluir matrículas trancadas.");
        }

        matriculaRepository.deleteById(id);

    }

}