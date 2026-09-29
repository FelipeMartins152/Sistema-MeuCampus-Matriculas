# Sistema MeuCampus - Serviço de Matrículas

## Anotações do Jackson utilizadas

O domain `Matricula` usa as seguintes anotações do Jackson para controlar como os dados são representados em JSON:

- **`@JsonProperty`** nos campos `alunoId`, `turmaId` e `dataMatricula`: usado para converter os nomes dos campos de 'camelCase' (padrão no Java) para 'snake_case' (`aluno_id`, `turma_id`, `data_matricula`) na representação em JSON, tanto nas requisições quanto nas respostas da API e no arquivo de dados. Tendo em vista que 'snake_case' foi a convenção de nomenclatura escolhida para o JSON deste serviço.

- **`@JsonFormat`** no campo `dataMatricula`: usado para fixar o formato de leitura e escrita da data como `yyyy-MM-dd`, de acordo com o formato ISO 8601. Como o JSON não possui um tipo nativo para data, toda data trafega como String e para evitar erros, como confundir dia e mês, é utilizado esse padrão de formato.

- **`@JsonIgnoreProperties(ignoreUnknown = true)`** na classe: usado para que a leitura fique tolerante a campos extras não mapeados no domain, evitando que o serviço quebre caso o JSON (do arquivo de dados ou de alguma requisição) tenha algum campo desconhecido pela classe. É mantido por boa prática, para que fique configurado na classe, não dependendo da configuração que o Spring Boot já faz para o Jackson para ignorar campos extras.

## O que cada integrante desenvolveu

- **Carolina Pinheiro**: camada web, tratamento global de exceções e documentação do projeto. Foi responsável pela criação do `MatriculaController`, mapeando os 5 endpoints REST (`GET`, `POST`, `PUT`, `DELETE`), o suporte a filtros via query string (`aluno_id`, `turma_id`, `situacao`), a validação das requisições com `@Valid` e a devolução dos códigos de status HTTP adequados (`200`, `201`, `204`, `404`). Desenvolveu também o `GlobalExceptionHandler` (`@RestControllerAdvice`) para captura centralizada e formatação estruturada em JSON de erros de validação e regras de negócio, além da elaboração e estruturação da documentação no `README.md`.

- **Felipe Martins**: camada de acesso a dados (`MatriculaRepository`), implementando as operações de listar, buscar, cadastrar, atualizar e remover, e o controle de concorrência com `synchronized`, além da configuração das anotações do Jackson no domain `Matricula`, responsáveis pela leitura e gravação do arquivo `dados/matriculas.json`.

- **Ricardo Bitencourt**: modelo de domínio e regras de negócio. Foram feitos `Matricula`, o enum `SituacaoMatricula`, os serviços de cadastro, atualização, busca, exclusão e listagem, além da validação que impede duas matrículas ativas para o mesmo aluno na mesma turma. A listagem permite filtrar por `alunoId`, `turmaId` e `situacao`.

### Integração com a camada web

O controller deve chamar os serviços, sem acessar o repositório diretamente:

- `ListarMatriculasService.executar(alunoId, turmaId, situacao)` recebe filtros opcionais; valores nulos são ignorados.
- `BuscarMatriculaPorIdService.executar(id)` e `AtualizarMatriculaService.executar(id, matricula)` retornam `Optional`; resultado vazio indica matrícula inexistente.
- `ExcluirMatriculaService.executar(id)` retorna `false` se não encontrou a matrícula.
- `RegraNegocioException` indica tentativa de criar ou atualizar uma matrícula ativa duplicada e deve ser convertida em resposta HTTP de erro pelo `RestControllerAdvice`.
- O controller deve validar o corpo das requisições com `@Valid` e deixar o repositório exclusivamente para os serviços.
