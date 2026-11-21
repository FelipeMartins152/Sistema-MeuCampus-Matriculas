package com.meucampus.matriculas.service;

import com.meucampus.matriculas.client.CadastroClient;
import com.meucampus.matriculas.client.TurmasClient;
import com.meucampus.matriculas.domain.Matricula;
import com.meucampus.matriculas.dto.AlunoDTO;
import com.meucampus.matriculas.dto.MatriculaDetalhesDTO;
import com.meucampus.matriculas.dto.TurmaDTO;
import com.meucampus.matriculas.exception.RecursoNaoEncontradoException;
import com.meucampus.matriculas.repository.MatriculaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BuscarMatriculaPorIdService {

    private final MatriculaRepository matriculaRepository;
    private final CadastroClient cadastroClient;
    private final TurmasClient turmasClient;

    public BuscarMatriculaPorIdService(
            MatriculaRepository matriculaRepository,
            CadastroClient cadastroClient,
            TurmasClient turmasClient) {
        this.matriculaRepository = matriculaRepository;
        this.cadastroClient = cadastroClient;
        this.turmasClient = turmasClient;
    }

    public Optional<Matricula> executar(long id) {
        return matriculaRepository.findById(id);
    }

    public MatriculaDetalhesDTO buscarDetalhes(Long id) {

        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "A matrícula " + id + " não foi encontrada."));

        AlunoDTO aluno = cadastroClient.buscarAluno(matricula.getAlunoId());
        TurmaDTO turma = turmasClient.buscarTurma(matricula.getTurmaId());

        return new MatriculaDetalhesDTO(
                matricula.getId(),
                matricula.getAlunoId(),
                aluno.getNome(),
                matricula.getTurmaId(),
                turma.getCodigo(),
                turma.getNome(),
                matricula.getDataMatricula(),
                matricula.getSituacao());
    }
    
}