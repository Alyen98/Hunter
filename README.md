# Hunter API

Bem-vindo à Hunter API, uma API RESTful desenvolvida com Quarkus para gerenciar entidades do universo de Hunter x Hunter. Através dela, é possível cadastrar, consultar, atualizar e deletar Hunters, suas habilidades Nen (Cards), Exames e Licenças.

## Features

  * **Gerenciamento Completo de Entidades:** Operações CRUD para Hunters, Cards, Exames e Licenças Hunter.
  * **API RESTful com HATEOAS:** Respostas em JSON que incluem links para facilitar a navegação e descoberta de recursos.
  * **Busca Avançada:** Endpoint de busca para Cards com suporte a filtro por termo, ordenação e paginação.
  * **Criação Aninhada de Recursos:** Crie um Hunter, suas habilidades (Cards) e associe-o a Exames em uma única requisição.
  * **Paginação e Ordenação:** Suporte completo para paginação e ordenação na maioria dos endpoints de listagem.
  * **Validação de Dados:** Utiliza Bean Validation para garantir a integridade dos dados na entrada.
  * **Banco de Dados Pré-populado:** Inclui um arquivo `import.sql` que popula o banco de dados com personagens e dados icônicos do anime, facilitando testes e demonstrações.

## Tecnologia Utilizada

  * **Java**
  * **Quarkus:** Framework Java nativo para nuvem, otimizado para JVM e compilação nativa.
  * **JAX-RS (RESTEasy Reactive):** Para a construção dos endpoints REST.
  * **Hibernate ORM com Panache:** Para simplificar o acesso e a manipulação de dados com JPA.
  * **MicroProfile OpenAPI:** Para geração automática de documentação da API.
  * **Maven:** Para gerenciamento de dependências e build do projeto.
  * **H2 (Banco de Dados em Memória):** Padrão do Quarkus para o perfil de desenvolvimento.

## Como Começar

Siga os passos abaixo para clonar e executar a aplicação localmente.

### Pré-requisitos

  * JDK 17 ou superior.
  * Apache Maven 3.8.1 ou superior.

### Executando a Aplicação

1.  **Clone o repositório:**

    ```bash
    git clone https://github.com/Alyen98/Hunter.git
    cd <NOME_DO_PROJETO>
    ```

2.  **Execute em modo de desenvolvimento:**
    O Quarkus oferece um modo de desenvolvimento com live reload, que é extremamente produtivo.

    ```bash
    ./mvnw quarkus:dev
    ```

3.  **Acesse a API:**
    A aplicação estará disponível em `http://localhost:8080`. O banco de dados será automaticamente populado com os dados do arquivo `src/main/resources/import.sql`.

4.  **Explore a Documentação (Swagger UI):**
    A documentação da API, gerada automaticamente, pode ser acessada em:
    [http://localhost:8080/q/swagger-ui](https://www.google.com/search?q=http://localhost:8080/q/swagger-ui)

## Documentação da API

A seguir estão os principais endpoints disponíveis.

### Hunters (`/hunters`)

Recurso para gerenciar os Hunters.

  * **`GET /hunters`**: Lista todos os hunters com paginação.

      * Query Params: `page` (página, default 1), `size` (itens por página, default 10).

  * **`GET /hunters/{id}`**: Busca um hunter pelo seu ID.

  * **`GET /hunters/{id}/cards`**: Lista todos os cards (habilidades) de um hunter específico.

  * **`POST /hunters`**: Cria um novo hunter (requisição simples).

      * Corpo da Requisição:
        ```json
        {
          "name": "Silva Zoldyck",
          "age": 46
        }
        ```

  * **`POST /hunters/full`**: Cria um hunter com seus cards e o associa a exames existentes em uma única chamada.

      * Corpo da Requisição (usando o `HunterCreationRequest`):
        ```json
        {
          "name": "Chrollo Lucilfer",
          "age": 26,
          "cards": [
            {
              "nenAbility": "Skill Hunter",
              "nenType": "Especialista",
              "exam": "Yorknew City Arc"
            }
          ],
          "examIds": [1, 3]
        }
        ```

  * **`PUT /hunters/{id}`**: Atualiza os dados de um hunter existente.

  * **`DELETE /hunters/{id}`**: Deleta um hunter pelo seu ID.

### Cards (`/cards`)

Recurso para gerenciar os Cards de habilidades Nen.

  * **`GET /cards`**: Lista todos os cards com paginação.

  * **`GET /cards/{id}`**: Busca um card pelo seu ID.

  * **`GET /cards/search`**: Realiza uma busca avançada por cards.

      * Query Params:
          * `q`: Termo para buscar no nome do hunter, habilidade ou tipo de Nen.
          * `sort`: Campo para ordenação (`id`, `hunter.name`, etc.).
          * `direction`: Direção da ordenação (`asc` ou `desc`).
          * `page`, `size`: Para paginação.

  * **`POST /cards`**: Cria um novo card. É necessário associá-lo a um hunter existente.

      * Corpo da Requisição:
        ```json
        {
          "nenAbility": "Remote Punch",
          "nenType": "Emissor",
          "exam": "287th Hunter Exam",
          "hunter": {
            "id": 4
          }
        }
        ```

  * **`PUT /cards/{id}`**: Atualiza um card existente.

  * **`DELETE /cards/{id}`**: Deleta um card.

### Exams (`/exams`)

Recurso para gerenciar os Exames Hunter.

  * **`GET /exams`**: Lista todos os exames.

  * **`GET /exams/{id}`**: Busca um exame pelo seu ID.

  * **`POST /exams`**: Cria um novo exame.

  * **`PUT /exams/{id}`**: Atualiza um exame existente.

  * **`DELETE /exams/{id}`**: Deleta um exame.

  * **`POST /exams/{examId}/hunters/{hunterId}`**: Adiciona um hunter como participante de um exame.

### Licenses (`/licenses`)

Recurso para visualizar as Licenças Hunter.

  * **`GET /licenses`**: Lista todas as licenças.

  * **`GET /licenses/{id}`**: Busca uma licença pelo seu ID.

## Modelo de Dados

As entidades principais e seus relacionamentos são:

  * **`Hunter`**: A entidade central.
      * `OneToOne` com `HunterLicense` (um hunter tem uma licença).
      * `OneToMany` com `Card` (um hunter pode ter vários cards).
      * `ManyToMany` com `Exam` (um hunter pode participar de vários exames).
  * **`Card`**: Representa uma habilidade Nen.
      * `ManyToOne` com `Hunter` (um card pertence a um hunter).
  * **`Exam`**: Representa um evento.
      * `ManyToMany` com `Hunter` (um exame pode ter vários participantes).
  * **`HunterLicense`**: A licença.
      * `OneToOne` com `Hunter` (uma licença pertence a um hunter).
