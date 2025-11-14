# Hunter API

Bem-vindo à Hunter API, uma API RESTful completa e robusta, desenvolvida com **Quarkus** para gerenciar entidades do universo de Hunter x Hunter. Através dela, é possível gerenciar Hunters, suas habilidades Nen (Cards), Exames e Licenças, com foco em segurança e resiliência de API.



## Destaques & Features

* **Gerenciamento Completo de Entidades:** Operações CRUD para Hunters, Cards, Exames e Licenças Hunter.
* **API RESTful com HATEOAS:** Respostas em JSON que incluem links para facilitar a navegação e descoberta de recursos.
* **Busca Avançada:** Endpoint de busca para Cards com suporte a filtro por termo, ordenação e paginação.
* **Criação Aninhada de Recursos (`/hunters/full`):** Crie um Hunter, suas habilidades (Cards) e associe-o a Exames existentes em uma única requisição.
* **Idempotência (`Idempotency-Key`):** Garante que operações `POST` sejam processadas apenas uma vez, prevenindo duplicações (Status **409 Conflict** em repetições).
* **Rate Limiting:** Limitação de taxa por cliente/IP, protegendo a API contra abuso e sobrecarga (Status **429 Too Many Requests**).
* **Validação e Tratamento de Erros:** Dados de entrada validados via **Bean Validation** e tratamento global de exceções para respostas claras e padronizadas (Status **400** e **500**).
* **CORS Configurado:** Política de Cross-Origin Resource Sharing configurada de forma segura para ambientes de frontend específicos.
* **Banco de Dados Pré-populado:** Inclui um arquivo `import.sql` que popula o banco de dados com personagens e dados icônicos do anime.

## Tecnologia Utilizada

* **Java 21**
* **Quarkus (3.x):** Framework Java nativo para nuvem, otimizado para JVM e compilação nativa.
* **JAX-RS (RESTEasy Reactive):** Para a construção dos endpoints REST.
* **Hibernate ORM com Panache:** Para simplificar o acesso e a manipulação de dados com JPA.
* **MicroProfile OpenAPI & Swagger UI:** Para geração automática de documentação da API.
* **Hibernate Validator (Bean Validation):** Para garantir a integridade dos dados.
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
    git clone [https://github.com/Alyen98/Hunter.git](https://github.com/Alyen98/Hunter.git)
    cd <NOME_DO_PROJETO>
    ```

2.  **Execute em modo de desenvolvimento:**
    O Quarkus oferece um modo de desenvolvimento com live reload.

    ```bash
    ./mvnw quarkus:dev
    ```

3.  **Acesse a API:**
    A aplicação estará disponível em `http://localhost:8080`.

4.  **Explore a Documentação (Swagger UI):**
    A documentação da API, gerada automaticamente, pode ser acessada em:
    `http://localhost:8080/q/swagger-ui`

## Configuração de Segurança

### Idempotência

Para garantir que uma operação `POST` não seja executada mais de uma vez, inclua o cabeçalho `Idempotency-Key` com um valor único (ex: UUID) na requisição.

| Status | Comportamento |
| :--- | :--- |
| **Primeira Requisição** | A operação é executada, a chave é registrada. |
| **Requisição Repetida** | Retorna **409 Conflict** e a operação não é reexecutada. |

### Rate Limiting

A API impõe um limite de requisições por cliente/IP. Respostas de sucesso incluem os seguintes cabeçalhos informativos:

| Cabeçalho | Descrição |
| :--- | :--- |
| **`X-RateLimit-Limit`** | O número máximo de requisições permitido por janela. |
| **`X-RateLimit-Remaining`** | O número de requisições restantes na janela atual. |
| **`X-RateLimit-Reset`** | O tempo (em segundos Unix Epoch) em que o limite será totalmente reiniciado. |

---

## Documentação da API

A seguir estão os principais endpoints disponíveis.

### Hunters (`/hunters`)

Recurso para gerenciar os Hunters.

* **`GET /hunters`**: Lista todos os hunters com paginação.
* **`GET /hunters/{id}`**: Busca um hunter pelo seu ID.
* **`GET /hunters/{id}/cards`**: Lista todos os cards (habilidades) de um hunter específico.
* **`POST /hunters`**: Cria um novo hunter (requisição simples).
* **`POST /hunters/full`**: Cria um hunter com seus cards e associações a exames existentes em uma única chamada.

    * Corpo da Requisição (Hunter Creation Request):
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
* **`DELETE /hunters/{id}`**: Deleta um hunter pelo seu ID (deleta Cards e Licença associados).

### Cards (`/cards`)

Recurso para gerenciar os Cards de habilidades Nen.

* **`GET /cards`**: Lista todos os cards com paginação.
* **`GET /cards/{id}`**: Busca um card pelo seu ID.
* **`GET /cards/search`**: Realiza uma **busca avançada** por cards, suportando `q` (termo de busca), `sort` (campo de ordenação), `direction` e paginação.
* **`POST /cards`**: Cria um novo card. É necessário associá-lo a um hunter existente (via ID do hunter no corpo da requisição).
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

As entidades principais e seus relacionamentos:

* **`Hunter`**:
    * `OneToOne` com `HunterLicense` (um hunter tem uma licença).
    * `OneToMany` com `Card` (um hunter pode ter vários cards).
    * `ManyToMany` com `Exam` (um hunter pode participar de vários exames).
* **`Card`**: Representa uma habilidade Nen.
    * `ManyToOne` com `Hunter`.
* **`Exam`**: Representa um evento.
    * `ManyToMany` com `Hunter`.
* **`HunterLicense`**: A licença.
    * `OneToOne` com `Hunter`.
