# Sistema MeuCampus - Serviço de Matrículas

## Anotações do Jackson utilizadas

O domain `Matricula` usa as seguintes anotações do Jackson para controlar como os dados são representados em JSON:

- **`@JsonProperty`** nos campos `alunoId`, `turmaId` e `dataMatricula`: usado para converter os nomes dos campos de 'camelCase' (padrão no Java) para 'snake_case' (`aluno_id`, `turma_id`, `data_matricula`) na representação em JSON, tanto nas requisições quanto nas respostas da API e no arquivo de dados. Tendo em vista que 'snake_case' foi a convenção de nomenclatura escolhida para o JSON deste serviço.

- **`@JsonIgnoreProperties(ignoreUnknown = true)`** na classe: usado para que a leitura fique tolerante a campos extras não mapeados no domain, evitando que o serviço quebre caso o JSON (do arquivo de dados ou de alguma requisição) tenha algum campo desconhecido pela classe.

**`@JsonFormat`** no campo `dataMatricula`: usado para deixar evidente o formato de leitura e escrita da data como `yyyy-MM-dd`, mesmo sendo o formato padrão utilizado para LocalDate pelo Jackson.

## O que cada integrante desenvolveu

- **Carolina Pinheiro**:

- **Felipe Martins**: camada de acesso a dados (`MatriculaRepository`) e configuração das anotações do Jackson no domain `Matricula`, responsáveis pela leitura e gravação do arquivo `dados/matriculas.json`.

- **Ricardo Bitencourt**:
