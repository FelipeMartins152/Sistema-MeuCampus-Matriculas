package com.meucampus.matriculas.service;

import com.meucampus.matriculas.repository.MatriculaRepository;
import org.springframework.stereotype.Service;

@Service
public class ExcluirMatriculaService {

    private final MatriculaRepository matriculaRepository;

    public ExcluirMatriculaService(MatriculaRepository matriculaRepository) {

        this.matriculaRepository = matriculaRepository;
        
    }

    public boolean executar(long id) {

        return matriculaRepository.remover(id);

    }

}