package com.meucampus.matriculas.client;

import com.meucampus.matriculas.dto.AlunoDTO;
import com.meucampus.matriculas.exception.RecursoRemotoNaoEncontradoException;
import com.meucampus.matriculas.exception.ServicoIndisponivelException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class CadastroClient {

    private final RestClient restClient;

    public CadastroClient(@Qualifier("cadastroRestClient") RestClient restClient) {

        this.restClient = restClient;

    }

    public AlunoDTO buscarAluno(Long id) {
        
        try {
            return restClient.get()
                    .uri("/alunos/{id}", id)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        if (response.getStatusCode().value() == 404) {
                            throw new RecursoRemotoNaoEncontradoException(
                                    "O aluno " + id + " não foi encontrado.");
                        }
                        throw new RestClientException("Erro ao consultar o serviço de Cadastro.");
                    })
                    .body(AlunoDTO.class);
        } catch (RecursoRemotoNaoEncontradoException exception) {
            throw exception;
        } catch (ResourceAccessException exception) {
            throw new ServicoIndisponivelException(
                    "O serviço de Cadastro está fora do ar ou não respondeu a tempo.");
        } catch (RestClientException exception) {
            throw new ServicoIndisponivelException(
                    "Falha ao consultar o serviço de Cadastro.");
        }

    }

}
