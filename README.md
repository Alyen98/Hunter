
## API Hunter x Hunter
Bem-vindo à API Hunter x Hunter, um serviço web RESTful construído com Quarkus. Esta API permite que você gerencie e recupere informações sobre Hunters, suas habilidades Nen (Cards), Exames Hunter e Licenças oficiais do universo de Hunter x Hunter.

O projeto está configurado com um banco de dados H2 em memória que é pré-carregado com dados iniciais, facilitando a execução e o teste dos endpoints imediatamente. Ele também apresenta uma interface Swagger UI com tema customizado para documentação interativa da API.

## Tecnologias Utilizadas
Quarkus - Framework Java para desenvolvimento de microsserviços e aplicações nativas em nuvem.

Recursos Disponíveis
Hunters: Cadastro, consulta, atualização e exclusão de Hunters.

Cards (Habilidades Nen): Gerenciamento completo das habilidades Nen dos Hunters.

Exames: Administração dos Exames Hunter e outros eventos.

Licenças Hunter: Consulta das licenças oficiais dos Hunters.

## Endpoints Principais
Hunters (/hunters)

GET /hunters - Lista todos os hunters com paginação.

POST /hunters - Cria um novo hunter.

POST /hunters/full - Cria um hunter com seus cards e exames associados.

GET /hunters/{id} - Retorna informações de um hunter específico.

PUT /hunters/{id} - Atualiza dados de um hunter.

DELETE /hunters/{id} - Exclui um hunter.

GET /hunters/{id}/cards - Lista os cards de um hunter específico.

Cards (/cards)

GET /cards - Lista todos os cards com paginação.

POST /cards - Cria um novo card para um hunter.

GET /cards/{id} - Retorna informações de um card específico.

PUT /cards/{id} - Atualiza dados de um card.

DELETE /cards/{id} - Exclui um card.

GET /cards/search - Pesquisa cards com filtros, ordenação e paginação.

Exames (/exams)

GET /exams - Lista todos os exames.

POST /exams - Cria um novo exame.

GET /exams/{id} - Retorna informações de um exame específico.

PUT /exams/{id} - Atualiza dados de um exame.

DELETE /exams/{id} - Exclui um exame.

POST /exams/{examId}/hunters/{hunterId} - Adiciona um hunter a um exame.

Licenças (/licenses)

GET /licenses - Lista todas as licenças.

GET /licenses/{id} - Retorna informações de uma licença específica.

## Documentação da API
Após executar a aplicação, acesse a documentação interativa:

Swagger UI: https://hunter-8ttu.onrender.com/q/swagger-ui/

## Desenvolvimento
Principais Entidades

Hunter: Representa os caçadores do universo HxH.

Card: Representa as habilidades Nen de um Hunter.

Exam: Representa os Exames Hunter e outros eventos importantes.

HunterLicense: Representa a licença oficial de um Hunter.

## Licença
Este projeto não possui licença.