# Sistema MeuCampus - Serviço de Matrículas

## Sumário
* [Trabalho 01](#1-trabalho-01)
* [Trabalho 02](#2-trabalho-02)

## 1. Trabalho 01

Serviço REST responsável por cadastrar, consultar, alterar e remover matrículas de alunos em turmas. Os dados são persistidos em um arquivo JSON (`dados/matriculas.json`), sem uso de banco de dados.

## Tecnologias utilizadas

- Java 17
- Spring Boot 3.5 (Spring Web e Bean Validation)
- Jackson (leitura e escrita de JSON)
- Lombok (redução de código repetitivo)
- Maven

## Como executar

Execute o comando a partir da **pasta raiz do projeto**, pois o caminho do arquivo de dados (`dados/matriculas.json`) é relativo ao diretório de execução:

```bash
./mvnw spring-boot:run        # Linux/macOS
mvnw.cmd spring-boot:run      # Windows
```

O serviço sobe na porta **8083** (`http://localhost:8083`). Caso o arquivo `dados/matriculas.json` não exista, ele é criado automaticamente com uma lista vazia.

## Estrutura do projeto

```
src/main/java/com/meucampus/matriculas/
├── MatriculasApplication.java        # ponto de entrada da aplicação
├── controller/
│   └── MatriculaController.java      # endpoints REST
├── service/
│   ├── CadastrarMatriculaService.java
│   ├── AtualizarMatriculaService.java
│   ├── BuscarMatriculaPorIdService.java
│   ├── ListarMatriculasService.java
│   ├── ExcluirMatriculaService.java
│   └── validator/
│       └── MatriculaUnicaValidator.java   # regra de matrícula ativa única
├── repository/
│   └── MatriculaRepository.java      # leitura e gravação do arquivo JSON
├── domain/
│   ├── Matricula.java                # modelo de dados
│   └── SituacaoMatricula.java        # enum ATIVA / TRANCADA
└── exception/
    ├── RegraNegocioException.java
    └── GlobalExceptionHandler.java   # tradução de erros em respostas HTTP
dados/
├── matriculas.json                   # arquivo de persistência
└── Sistema-MeuCampus-Matriculas.postman_collection.json
```

O fluxo de uma requisição segue sempre a ordem **Controller → Service → Repository → arquivo JSON**, e a resposta retorna pelo mesmo caminho. O controller nunca acessa o repositório diretamente.

## Modelo de dados

Uma matrícula possui os seguintes campos (nomes como aparecem no JSON):

| Campo            | Tipo     | Obrigatório | Observação                                        |
|------------------|----------|-------------|---------------------------------------------------|
| `id`             | número   | não         | Gerado automaticamente pelo serviço no cadastro   |
| `aluno_id`       | número   | sim         | Identificador do aluno                            |
| `turma_id`       | número   | sim         | Identificador da turma                            |
| `data_matricula` | texto    | sim         | Formato `yyyy-MM-dd` (ex.: `2026-09-20`)          |
| `situacao`       | texto    | não         | `ATIVA` ou `TRANCADA`; se omitida, assume `ATIVA` |

Exemplo:

```json
{
  "id": 1,
  "aluno_id": 1,
  "turma_id": 1,
  "data_matricula": "2026-09-20",
  "situacao": "ATIVA"
}
```

## Endpoints

| Método   | Rota                | Descrição                                   | Status de sucesso |
|----------|---------------------|---------------------------------------------|-------------------|
| `GET`    | `/matriculas`       | Lista matrículas (aceita filtros opcionais) | `200`             |
| `GET`    | `/matriculas/{id}`  | Busca uma matrícula pelo id                 | `200`             |
| `POST`   | `/matriculas`       | Cadastra uma nova matrícula                 | `201`             |
| `PUT`    | `/matriculas/{id}`  | Altera uma matrícula existente              | `200`             |
| `DELETE` | `/matriculas/{id}`  | Remove uma matrícula                        | `204`             |

**Filtros do `GET /matriculas`** (query string, todos opcionais e combináveis): `aluno_id`, `turma_id` e `situacao`.

Exemplo: `GET /matriculas?aluno_id=1&situacao=ATIVA`

Uma coleção do Postman com todas as requisições está disponível em `dados/Sistema-MeuCampus-Matriculas.postman_collection.json`.

## Regras de negócio

1. **Aluno, turma e data da matrícula são obrigatórios** no cadastro e na alteração.
2. **Uma matrícula não pode ser criada como `TRANCADA`.** Todo cadastro entra como `ATIVA`.
3. **Um aluno não pode ter duas matrículas `ATIVAS` na mesma turma.** A regra é verificada no cadastro e na alteração (quando a nova situação é `ATIVA`). Ao alterar, a própria matrícula é desconsiderada na comparação.
4. O `id` é sempre gerado pelo serviço (maior id existente + 1); um `id` enviado no corpo do cadastro é ignorado.

## Códigos de status e tratamento de erros

| Código | Significado  | Quando ocorre                                                                 |
|--------|--------------|-------------------------------------------------------------------------------|
| `200`  | OK           | Consulta ou alteração realizada com sucesso                                   |
| `201`  | Created      | Matrícula cadastrada                                                          |
| `204`  | No Content   | Matrícula removida                                                            |
| `400`  | Bad Request  | Campo obrigatório ausente, ou tentativa de cadastrar matrícula como `TRANCADA` |
| `404`  | Not Found    | Não existe matrícula com o id informado (busca, alteração ou remoção)         |
| `409`  | Conflict     | Aluno já possui matrícula `ATIVA` na turma                                    |

Os erros tratados pelo `GlobalExceptionHandler` seguem um formato padronizado:

```json
{
  "timestamp": "2026-09-29T10:15:30.123",
  "status": 409,
  "error": "Conflict",
  "message": "O aluno já possui uma matrícula ATIVA nesta turma."
}
```

Requisições com JSON malformado, data fora do formato ou `situacao` inexistente também resultam em `400`, tratadas pelo próprio Spring Boot.

## Persistência em arquivo JSON

- Ao iniciar, o `MatriculaRepository` lê o arquivo `dados/matriculas.json` e carrega as matrículas em memória (`@PostConstruct`). Também calcula o próximo id disponível. É por isso que **os dados permanecem intactos após reiniciar o serviço**.
- A cada cadastro, alteração ou remoção, o arquivo é reescrito por completo com a lista atualizada, formatado de forma legível.
- As operações que modificam os dados são `synchronized`, evitando que requisições simultâneas corrompam o arquivo ou gerem ids repetidos.

## Anotações do Jackson utilizadas

O Jackson é a biblioteca que converte objetos Java em JSON (serialização) e JSON em objetos Java (desserialização). O domain `Matricula` usa as seguintes anotações do Jackson para controlar como os dados são representados em JSON:

- **`@JsonProperty`** nos campos `alunoId`, `turmaId` e `dataMatricula`: usado para converter os nomes dos campos de 'camelCase' (padrão no Java) para 'snake_case' (`aluno_id`, `turma_id`, `data_matricula`) na representação em JSON, tanto nas requisições quanto nas respostas da API e no arquivo de dados. Tendo em vista que 'snake_case' foi a convenção de nomenclatura escolhida para o JSON deste serviço.

- **`@JsonFormat`** no campo `dataMatricula`: usado para fixar o formato de leitura e escrita da data como `yyyy-MM-dd`, de acordo com o formato ISO 8601. Como o JSON não possui um tipo nativo para data, toda data trafega como String e para evitar erros, como confundir dia e mês, é utilizado esse padrão de formato. Mesmo o Spring Boot já configurando o Jackson para escrever datas no formato ISO, a anotação deixa essa convenção explícita e garantida diretamente na classe `Matricula`, não dependendo de uma configuração global do projeto, que pode ser alterada em algum momento para outra situação e deixar o formato errado.

- **`@JsonIgnoreProperties(ignoreUnknown = true)`** na classe: usado para que a leitura fique tolerante a campos extras não mapeados no domain, evitando que o serviço quebre caso o JSON (do arquivo de dados ou de alguma requisição) tenha algum campo desconhecido pela classe. É mantido por boa prática, para que fique configurado na classe, não dependendo da configuração que o Spring Boot já faz para o Jackson para ignorar campos extras.

### Outras anotações presentes no domain (não pertencem ao Jackson)

- **`@NotNull`** (Jakarta Bean Validation): marca `aluno_id`, `turma_id` e `data_matricula` como obrigatórios. A validação é acionada pelo `@Valid` no controller e, se falhar, resulta em `400`.
- **`@Getter`, `@Setter`, `@Builder`, `@AllArgsConstructor`, `@NoArgsConstructor`** (Lombok): geram automaticamente getters, setters, construtores e builder. `@Builder.Default` faz a `situacao` iniciar como `ATIVA` quando não informada.

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



## 2. Trabalho 02

## Informações do Banco de Dados

- Banco de Dados: H2 Database
- Acesso ao H2 console: `http://localhost:8083/h2-console`
- Saved Settings: `Generic H2 (Embedded)`
- Saved Name: `Generic H2 (Embedded)`
- Driver Class: `org.h2.Driver`
- JDBC URL: `jdbc:h2:file:./banco/matriculas`
- User Name: `sa`
- Password: Deixar em branco

Os dados são mantidos no arquivo `banco/matriculas.mv.db` e continuam disponíveis depois de reiniciar o serviço. Nenhum dado de aluno ou turma é copiado para esse banco.

## Configuração dos serviços externos

Os endereços dos outros serviços são configurados em `application.properties`:

```properties
servicos.cadastro.url=http://localhost:8081
servicos.turmas.url=http://localhost:8082
servicos.http.connect-timeout=3000
servicos.http.read-timeout=3000
```

Não devem existir URLs externas fixas no código Java.

## Comunicação HTTP implementada

### Serviço de Cadastro

O `CadastroClient` consulta:

```text
GET http://localhost:8081/alunos/{id}
```

Essa chamada confirma que o aluno existe ao cadastrar uma matrícula, ao atualizar uma matrícula ativa ou reativar uma matrícula trancada.

### Serviço de Turmas

O `TurmasClient` consulta:

```text
GET http://localhost:8082/turmas/{id}
```

Essa chamada confirma que a turma existe e fornece o número de vagas da turma.

Os dados recebidos são representados pelos DTOs `AlunoDTO` e `TurmaDTO`. Os dados externos não são gravados no banco de Matrículas.

## Endpoint de consulta composta

Foi acrescentado o endpoint:

```text
GET /matriculas/{id}/detalhes
```

Ele reúne os dados locais da matrícula com:

- o nome do aluno, obtido no serviço de Cadastro;
- o código e o nome da disciplina da turma, obtidos no serviço de Turmas.

Os dados externos são buscados no momento da requisição e não são persistidos.

## Listagem e filtros

A listagem principal continua em `GET /matriculas`, agora com paginação e os filtros combinados:

```text
GET /matriculas?alunoId=1
GET /matriculas?turmaId=2
GET /matriculas?situacao=ativa
GET /matriculas?turmaId=2&situacao=ATIVA
GET /matriculas?alunoId=1&turmaId=2&situacao=ativa&page=0&size=10
```

Os filtros são enviados ao `MatriculaRepository` por consultas derivadas com `Pageable`. A situação aceita `ATIVA` ou `ativa`, independentemente das letras maiúsculas ou minúsculas.

## Regras de integridade da integração

1. Ao cadastrar ou reativar uma matrícula ativa, o sistema consulta o aluno no serviço de Cadastro.
2. Ao cadastrar ou reativar uma matrícula ativa, o sistema consulta a turma no serviço de Turmas.
3. Um `404` recebido dos serviços externos é traduzido para `400` no serviço de Matrículas.
4. Não é permitido cadastrar outra matrícula ativa para o mesmo aluno na mesma turma.
5. Uma nova matrícula ativa só é aceita quando existem vagas na turma.
6. Ao reativar uma matrícula trancada, o sistema compara o total de vagas com a quantidade de matrículas ativas locais.
7. Uma turma lotada devolve `409 Conflict`.
8. Falha de conexão, timeout ou serviço externo indisponível devolve `503 Service Unavailable`.

## Tratamento de erros

As respostas são centralizadas em `@RestControllerAdvice`.

| Código | Quando ocorre |
|---:|---|
| `400` | aluno ou turma inexistente, JSON inválido, campo inválido ou situação inválida |
| `404` | matrícula local inexistente |
| `409` | matrícula ativa duplicada, turma lotada ou exclusão inválida |
| `503` | Cadastro ou Turmas fora do ar ou não responder dentro do timeout |

Exemplo:

```json
{
  "timestamp": "2026-11-21T16:52:11",
  "status": 503,
  "error": "Service Unavailable",
  "message": "O serviço de Cadastro está fora do ar ou não respondeu a tempo."
}
```

## Documentação OpenAPI

A documentação foi configurada com `springdoc-openapi` na linha 2.8.x.

Após iniciar o serviço, ela fica disponível em:

```text
http://localhost:8083/swagger-ui/index.html
```

A documentação deve listar os filtros, o endpoint `/detalhes` e as respostas `400`, `404`, `409` e `503`.

## Anotações do Jackson utilizadas

O Jackson é a biblioteca que converte objetos Java em JSON (serialização) e JSON em objetos Java (desserialização). O domain `Matricula` usa as seguintes anotações do Jackson para controlar como os dados são representados em JSON:

- **`@JsonProperty`** nos campos `alunoId`, `turmaId` e `dataMatricula`: usado para converter os nomes dos campos de 'camelCase' (padrão no Java) para 'snake_case' (`aluno_id`, `turma_id`, `data_matricula`) na representação em JSON, tanto nas requisições quanto nas respostas da API e no arquivo de dados. Tendo em vista que 'snake_case' foi a convenção de nomenclatura escolhida para o JSON deste serviço.

- **`@JsonFormat`** no campo `dataMatricula`: usado para fixar o formato de leitura e escrita da data como `yyyy-MM-dd`, de acordo com o formato ISO 8601. Como o JSON não possui um tipo nativo para data, toda data trafega como String e para evitar erros, como confundir dia e mês, é utilizado esse padrão de formato. Mesmo o Spring Boot já configurando o Jackson para escrever datas no formato ISO, a anotação deixa essa convenção explícita e garantida diretamente na classe `Matricula`, não dependendo de uma configuração global do projeto, que pode ser alterada em algum momento para outra situação e deixar o formato errado.

- **`@JsonIgnoreProperties(ignoreUnknown = true)`** na classe: usado para que a leitura fique tolerante a campos extras não mapeados no domain, evitando que o serviço quebre caso o JSON (do arquivo de dados ou de alguma requisição) tenha algum campo desconhecido pela classe. É mantido por boa prática, para que fique configurado na classe, não dependendo da configuração que o Spring Boot já faz para o Jackson para ignorar campos extras.

## O que cada integrante desenvolveu

- **Carolina Pinheiro**:

- **Felipe Martins**: Atualização do `MatriculaRepository` para ser uma interface que utiliza o Spring Data JPA para fazer o mapeamento objeto-relacional. Atualização do domain `Matricula` para ser uma entidade com `@Entity`, `@Id`, `@GenerateValue` e `@Enumerated(EnumType.STRING)`. Configuração do banco de dados H2 e criação da classe `RegistrosDeExemplo`, responsável por criar alguns registros no banco ao inicializar a aplicação, caso nenhum dado tenha sido gravado ainda. 

- **Ricardo Bitencourt**: Implementação da comunicação HTTP com os serviços de Cadastro e Turmas utilizando `RestClient`. Configuração das URLs externas e dos timeouts em `application.properties`, criação dos DTOs `AlunoDTO`, `TurmaDTO` e `MatriculaDetalhesDTO`, tradução de respostas remotas `404` e de falhas de conexão, aplicação das regras de integridade no cadastro e na atualização, controle de vagas e duplicidade ativa, além da implementação da consulta composta que retorna os detalhes da matrícula com os dados do aluno e da turma.