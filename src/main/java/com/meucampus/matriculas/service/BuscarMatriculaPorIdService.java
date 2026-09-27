package com.meucampus.matriculas.service;

import com.meucampus.matriculas.domain.Matricula;
import com.meucampus.matriculas.repository.MatriculaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BuscarMatriculaPorIdService {

    private final MatriculaRepository matriculaRepository;

    public BuscarMatriculaPorIdService(MatriculaRepository matriculaRepository) {

        this.matriculaRepository = matriculaRepository;

    }

    public Optional<Matricula> executar(long id) {

        return matriculaRepository.buscarPorId(id);

    }

}