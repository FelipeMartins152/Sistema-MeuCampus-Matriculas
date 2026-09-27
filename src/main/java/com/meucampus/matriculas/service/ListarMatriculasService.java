package com.meucampus.matriculas.service;

import com.meucampus.matriculas.domain.Matricula;
import com.meucampus.matriculas.domain.SituacaoMatricula;
import com.meucampus.matriculas.repository.MatriculaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarMatriculasService {

    private final MatriculaRepository matriculaRepository;

    public ListarMatriculasService(MatriculaRepository matriculaRepository) {
        this.matriculaRepository = matriculaRepository;
    }

    public List<Matricula> executar(Long alunoId, Long turmaId, SituacaoMatricula situacao) {
        List<Matricula> todas = matriculaRepository.listar();

        return todas.stream()
                .filter(m -> alunoId == null || m.getAlunoId().equals(alunoId))
                .filter(m -> turmaId == null || m.getTurmaId().equals(turmaId))
                .filter(m -> situacao == null || m.getSituacao() == situacao)
                .toList();
    }
}