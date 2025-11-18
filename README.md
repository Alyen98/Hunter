# Hunter API | Gerenciamento de Hunters e Habilidades Nen

Bem-vindo à Hunter API, uma API RESTful completa e robusta, desenvolvida com **Quarkus** para gerenciar entidades do universo de Hunter x Hunter. A aplicação foi construída com foco em **segurança**, **performance** e **resiliência** de API.

---

## Destaques & Recursos de Nível Profissional

* **Gerenciamento Completo de Entidades (CRUD):** Hunters, Cards (Habilidades Nen), Exames e Licenças Hunter.
* **HATEOAS Integrado:** Respostas em JSON que incluem links de navegação (`_links`) para descoberta e consumo intuitivo dos recursos.
* **Busca Avançada de Cards:** Endpoint flexível (`/cards/search`) com suporte a filtro por termo (`q`), ordenação e paginação.
* **Criação Aninhada de Recursos:** O endpoint **`/hunters/full`** permite criar um Hunter, suas habilidades (Cards) e associá-lo a Exames existentes em uma única requisição.
* **Idempotência (`Idempotency-Key`):** Garante que operações críticas `POST` sejam processadas apenas uma vez, prevenindo duplicações. Retorna **`409 Conflict`** em repetições.
* **Rate Limiting:** Limitação de taxa por cliente/IP, protegendo a API contra abuso e sobrecarga. Retorna **`429 Too Many Requests`**.
* **Validação e Tratamento de Erros:** Dados de entrada validados via **Bean Validation** e tratamento global de exceções, retornando mensagens claras e padronizadas (Status `400` e `500`).
* **Banco de Dados População:** Inclui o script `import.sql` que popula o banco de dados inicial com personagens e dados icônicos.

---

## Tecnologia & Stack

| Área | Tecnologia | Detalhe |
| :--- | :--- | :--- |
| **Plataforma** | **Java 21** | Linguagem de desenvolvimento. |
| **Framework** | **Quarkus (3.x)** | Otimizado para JVM, conteinerização e compilação nativa (Cloud-Native). |
| **Web Services** | JAX-RS (RESTEasy Reactive) | Construção eficiente dos endpoints REST. |
| **Persistência** | Hibernate ORM com **Panache** | Simplificação do acesso e manipulação de dados (Active Record Pattern). |
| **Validação** | Hibernate Validator (Bean Validation) | Garantia de integridade e regras de negócio na camada de entrada. |
| **Documentação** | MicroProfile OpenAPI & Swagger UI | Geração automática da especificação da API. |
| **Build Tool** | Apache Maven | Gerenciamento de dependências e automação do ciclo de vida. |
| **Banco de Dados** | H2 (Em Memória) | Padrão para desenvolvimento e testes. |

---

## Guia de Início Rápido

### Pré-requisitos

* JDK 17 ou superior.
* Apache Maven 3.8.1 ou superior.

### Executando em Modo de Desenvolvimento (JVM)

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/Alyen98/Hunter.git](https://github.com/Alyen98/Hunter.git)
    cd <NOME_DO_PROJETO>
    ```

2.  **Execute o Quarkus em modo de desenvolvimento:**
    O modo `dev` oferece *live reload* para produtividade imediata.
    ```bash
    ./mvnw quarkus:dev
    ```

3.  **Acesse a Documentação (Swagger UI):**
    A API estará disponível em `http://localhost:8080`.
    Acesse a documentação interativa em: `http://localhost:8080/q/swagger-ui`

### Executando com Docker

O `Dockerfile` e o `mvnw` estão configurados para gerar uma imagem otimizada para JVM ou compilação nativa.

1.  **Construa a Imagem Docker (Usando a JDK 21 como base):**
    ```bash
    docker build -t hunter-api .
    ```
2.  **Execute o Container:**
    ```bash
    docker run -i --rm -p 8080:8080 hunter-api
    ```

---

## Detalhes de Segurança e Resiliência

### 1. Idempotência (Garantia de Não-Duplicação)

Para garantir que operações de alteração de estado (`POST`) não sejam executadas acidentalmente mais de uma vez, utilize o cabeçalho `Idempotency-Key` com um UUID único.

| Comportamento | Requisição | Resposta HTTP |
| :--- | :--- | :--- |
| **Primeira (Processada)** | `POST /hunters/full` com `Idempotency-Key: <UUID_A>` | **`201 Created`** |
| **Repetição (Bloqueada)** | `POST /hunters/full` com `Idempotency-Key: <UUID_A>` | **`409 Conflict`** |

### 2. Rate Limiting (Controle de Taxa)

A API impõe um limite de requisições por cliente/IP. Se o limite for excedido, o status **`429 Too Many Requests`** é retornado.

Para informar o cliente sobre seu uso atual, as respostas de sucesso incluem os seguintes cabeçalhos informativos:

| Cabeçalho | Descrição |
| :--- | :--- |
| **`X-RateLimit-Limit`** | O número máximo de requisições permitido na janela. |
| **`X-RateLimit-Remaining`** | O número de requisições restantes na janela atual. |
| **`X-RateLimit-Reset`** | O tempo (em segundos Unix Epoch) em que o limite será totalmente reiniciado. |

### 3. CORS Configurado

A política de CORS está definida em `application.properties` para permitir acesso seguro apenas de origens específicas, como `https://alyen98.github.io`, controlando métodos (`GET`, `POST`, `PUT`, `DELETE`) e cabeçalhos permitidos.

---

## Modelo de Dados (Estrutura e Relações)

As entidades principais e seus relacionamentos JPA:

* **`Hunter`**: Entidade central.
    * `OneToOne` com `HunterLicense`.
    * `OneToMany` com `Card` (Permite múltiplos Cards).
    * `ManyToMany` com `Exam` (Permite múltiplos Exames).
* **`Card`**: Representa uma habilidade Nen.
    * `ManyToOne` com `Hunter`.
* **`Exam`**: Representa um evento.
    * `ManyToMany` com `Hunter`.
* **`HunterLicense`**: A licença.
    * `OneToOne` com `Hunter`.

---

## Documentação da API

### Hunters (`/hunters`)

| Método | Path | Descrição | Corpo/Detalhes |
| :--- | :--- | :--- | :--- |
| `GET` | `/hunters` | Lista todos os hunters com paginação. | Retorna `HunterRepresentation` (HATEOAS). |
| `GET` | `/hunters/{id}` | Busca um hunter pelo seu ID. | |
| `GET` | `/hunters/{id}/cards` | Lista todos os cards (habilidades) de um hunter específico. | |
| `POST` | `/hunters` | Cria um novo hunter (simples). | Corpo: Objeto `Hunter` com `name` e `age`. |
| `POST` | `/hunters/full` | Cria Hunter com Cards aninhados e associa a `examIds`. | Usa o DTO `HunterCreationRequest` (veja exemplo abaixo). |
| `PUT` | `/hunters/{id}` | Atualiza nome e idade. | |
| `DELETE` | `/hunters/{id}` | Deleta o hunter. **(Cascade: Cards e Licença são deletados)**. | |

**Exemplo de Requisição `POST /hunters/full`:**

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
````

### Cards (`/cards`)

  * **`GET /cards`**: Lista todos os cards com paginação.
  * **`GET /cards/{id}`**: Busca um card pelo seu ID.
  * **`GET /cards/search`**: Realiza uma **busca avançada** (Query Params: `q`, `sort`, `direction`, `page`, `size`).
  * **`POST /cards`**: Cria um novo card. **Requer o ID do Hunter no corpo**.
  * **`PUT /cards/{id}`**: Atualiza um card existente.
  * **`DELETE /cards/{id}`**: Deleta um card.

### Exams (`/exams`)

  * **`GET /exams`**: Lista todos os exames.
  * **`GET /exams/{id}`**: Busca um exame pelo seu ID.
  * **`POST /exams`**: Cria um novo exame.
  * **`PUT /exams/{id}`**: Atualiza um exame existente.
  * **`DELETE /exams/{id}`**: Deleta um exame.
  * **`POST /exams/{examId}/hunters/{hunterId}`**: Adiciona um hunter como participante de um exame.

### Licenses (`/licenses`)

  * **`GET /licenses`**: Lista todas as licenças.
  * **`GET /licenses/{id}`**: Busca uma licença pelo seu ID.

<!-- end list -->

```
```
