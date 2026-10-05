# TaskMaster API

API REST de gerenciamento de tarefas em Java com Spring Boot. Projeto de estudo com regras de negócio, validação, paginação, documentação Swagger e testes unitários e funcionais.

<p>
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot">
  <img src="https://img.shields.io/badge/H2-09476B?style=for-the-badge&logoColor=white" alt="H2">
  <img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black" alt="Swagger">
</p>

## Endpoints

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/tasks` | Cria uma tarefa |
| GET | `/api/tasks` | Lista as tarefas, com paginação |
| GET | `/api/tasks/{id}` | Busca uma tarefa |
| GET | `/api/tasks/search?categoria=` | Filtra por categoria, com paginação |
| PATCH | `/api/tasks/{id}/concluir` | Marca a tarefa como concluída |
| PUT | `/api/tasks/{id}` | Atualiza uma tarefa |
| DELETE | `/api/tasks/{id}` | Exclui uma tarefa |

Uma tarefa tem título, descrição, prioridade (baixa, média ou alta), data limite, categoria e a marcação de concluída.

## Regras de negócio

- Título, prioridade, data limite e categoria são obrigatórios
- A data limite não pode ser anterior à data atual
- Tarefa concluída não pode ser editada nem excluída
- Tarefa já concluída não pode ser concluída de novo

Os erros voltam em formato padronizado, por um tratador global de exceções.

## Como rodar

Pré-requisito: JDK 17. O banco é o H2 em memória, então não há nada para instalar.

```bash
git clone https://github.com/Juan-Souzaa/taskmaster.git
cd taskmaster
./mvnw spring-boot:run
```

| Recurso | Endereço |
|---|---|
| API | http://localhost:8080/api/tasks |
| Swagger | http://localhost:8080/swagger-ui.html |
| Console do H2 | http://localhost:8080/h2-console |

## Testes

```bash
./mvnw test
```

Há testes unitários do serviço e do controller e testes funcionais dos endpoints.
