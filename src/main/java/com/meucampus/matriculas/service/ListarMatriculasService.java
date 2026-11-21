package com.meucampus.matriculas.service;

import com.meucampus.matriculas.domain.Matricula;
import com.meucampus.matriculas.domain.SituacaoMatricula;
import com.meucampus.matriculas.repository.MatriculaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListarMatriculasService {

    private final MatriculaRepository matriculaRepository;

    public ListarMatriculasService(MatriculaRepository matriculaRepository) {
        this.matriculaRepository = matriculaRepository;
    }

    public Page<Matricula> executar(Long alunoId, Long turmaId, SituacaoMatricula situacao, Pageable pageable) {
        if (alunoId != null && turmaId != null && situacao != null) {
            return matriculaRepository.findByAlunoIdAndTurmaIdAndSituacao(alunoId, turmaId, situacao, pageable);
        }
        if (alunoId != null && turmaId != null) {
            return matriculaRepository.findByAlunoIdAndTurmaId(alunoId, turmaId, pageable);
        }
        if (alunoId != null && situacao != null) {
            return matriculaRepository.findByAlunoIdAndSituacao(alunoId, situacao, pageable);
        }
        if (turmaId != null && situacao != null) {
            return matriculaRepository.findByTurmaIdAndSituacao(turmaId, situacao, pageable);
        }
        if (alunoId != null) {
            return matriculaRepository.findByAlunoId(alunoId, pageable);
        }
        if (turmaId != null) {
            return matriculaRepository.findByTurmaId(turmaId, pageable);
        }
        if (situacao != null) {
            return matriculaRepository.findBySituacao(situacao, pageable);
        }
        return matriculaRepository.findAll(pageable);
    }
    
}