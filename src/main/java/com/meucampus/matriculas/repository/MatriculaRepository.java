package com.meucampus.matriculas.repository;

import com.meucampus.matriculas.domain.Matricula;
import com.meucampus.matriculas.domain.SituacaoMatricula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    Optional<Matricula> findById(Long id);

    Page<Matricula> findAll(Pageable pageable);

    List<Matricula> findByAlunoId(Long alunoId);

    List<Matricula> findByTurmaId(Long turmaId);

    List<Matricula> findBySituacao(SituacaoMatricula situacao);

    List<Matricula> findByAlunoIdAndSituacao(Long alunoId, SituacaoMatricula situacao);

    List<Matricula> findByTurmaIdAndSituacao(Long turmaId, SituacaoMatricula situacao);

    List<Matricula> findByAlunoIdAndTurmaId(Long alunoId, Long turmaId);

    List<Matricula> findByAlunoIdAndTurmaIdAndSituacao(Long alunoId, Long turmaId, SituacaoMatricula situacao);

    boolean existsByAlunoIdAndTurmaIdAndSituacao(Long alunoId, Long turmaId, SituacaoMatricula situacao);

    long countByTurmaIdAndSituacao(Long turmaId, SituacaoMatricula situacao);
}