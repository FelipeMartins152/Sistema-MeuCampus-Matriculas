package com.meucampus.matriculas.repository;

import com.meucampus.matriculas.domain.Matricula;
import com.meucampus.matriculas.domain.SituacaoMatricula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    Page<Matricula> findByAlunoId(Long alunoId, Pageable pageable);

    Page<Matricula> findByTurmaId(Long turmaId, Pageable pageable);

    Page<Matricula> findBySituacao(SituacaoMatricula situacao, Pageable pageable);

    Page<Matricula> findByAlunoIdAndSituacao(Long alunoId, SituacaoMatricula situacao, Pageable pageable);

    Page<Matricula> findByTurmaIdAndSituacao(Long turmaId, SituacaoMatricula situacao, Pageable pageable);

    Page<Matricula> findByAlunoIdAndTurmaId(Long alunoId, Long turmaId, Pageable pageable);

    Page<Matricula> findByAlunoIdAndTurmaIdAndSituacao(Long alunoId, Long turmaId, SituacaoMatricula situacao, Pageable pageable);

    List<Matricula> findByAlunoIdAndTurmaIdAndSituacao(Long alunoId, Long turmaId, SituacaoMatricula situacao);

    boolean existsByAlunoIdAndTurmaIdAndSituacao(Long alunoId, Long turmaId, SituacaoMatricula situacao);

    long countByTurmaIdAndSituacao(Long turmaId, SituacaoMatricula situacao);
    
}