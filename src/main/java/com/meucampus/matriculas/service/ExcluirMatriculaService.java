package com.meucampus.matriculas.service;

import com.meucampus.matriculas.repository.MatriculaRepository;
import org.springframework.stereotype.Service;
import com.meucampus.matriculas.domain.Matricula;
import com.meucampus.matriculas.domain.SituacaoMatricula;
import com.meucampus.matriculas.exception.RegraNegocioException;

@Service
public class ExcluirMatriculaService {

    private final MatriculaRepository matriculaRepository;

    public ExcluirMatriculaService(MatriculaRepository matriculaRepository) {

        this.matriculaRepository = matriculaRepository;
        
    }

    public boolean executar(long id) {
        Matricula matricula = matriculaRepository.buscarPorId(id)
                .orElse(null);

        if (matricula == null) {
            return false;
        }

        if (matricula.getSituacao() != SituacaoMatricula.TRANCADA) {
            throw new RegraNegocioException("Só é possível excluir matrículas trancadas.");
        }

        return matriculaRepository.remover(id);
    }

}