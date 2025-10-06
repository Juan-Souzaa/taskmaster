package com.taskmaster.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskControllerFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Test
    void deveCriarTarefaComSucesso() throws Exception {
        String json = """
                {
                    "titulo": "Tarefa de Teste",
                    "descricao": "Descrição da tarefa de teste",
                    "prioridade": "MEDIA",
                    "dataLimite": "%s",
                    "categoria": "Trabalho"
                }
                """.formatted(LocalDateTime.now().plusDays(1).format(formatter));

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Tarefa de Teste"))
                .andExpect(jsonPath("$.descricao").value("Descrição da tarefa de teste"))
                .andExpect(jsonPath("$.prioridade").value("MEDIA"))
                .andExpect(jsonPath("$.categoria").value("Trabalho"))
                .andExpect(jsonPath("$.concluida").value(false))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void deveRetornarErro400AoCriarTarefaComDataLimiteInvalida() throws Exception {
        String json = """
                {
                    "titulo": "Tarefa de Teste",
                    "descricao": "Descrição da tarefa de teste",
                    "prioridade": "MEDIA",
                    "dataLimite": "%s",
                    "categoria": "Trabalho"
                }
                """.formatted(LocalDateTime.now().minusDays(1).format(formatter));

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Dados inválidos"));
    }

    @Test
    void deveRetornarErro400AoCriarTarefaComCamposObrigatoriosFaltando() throws Exception {
        String json = """
                {
                    "descricao": "Descrição da tarefa de teste",
                    "prioridade": "MEDIA",
                    "dataLimite": "%s"
                }
                """.formatted(LocalDateTime.now().plusDays(1).format(formatter));

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Dados inválidos"))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void deveRetornarNotFoundAoBuscarTarefaInexistente() throws Exception {
        mockMvc.perform(get("/api/tasks/9999"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Recurso não encontrado"));
    }

    @Test
    void deveListarTarefasComPaginacao() throws Exception {
        // Primeiro cria uma tarefa
        String json = """
                {
                    "titulo": "Tarefa para Listagem",
                    "descricao": "Descrição da tarefa",
                    "prioridade": "ALTA",
                    "dataLimite": "%s",
                    "categoria": "Estudo"
                }
                """.formatted(LocalDateTime.now().plusDays(1).format(formatter));

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());

        // Depois lista as tarefas
        mockMvc.perform(get("/api/tasks?page=0&size=10&sort=titulo,asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable").exists())
                .andExpect(jsonPath("$.totalElements").exists());
    }

    @Test
    void deveBuscarTarefasPorCategoria() throws Exception {
        // Primeiro cria uma tarefa
        String json = """
                {
                    "titulo": "Tarefa de Trabalho",
                    "descricao": "Descrição da tarefa",
                    "prioridade": "BAIXA",
                    "dataLimite": "%s",
                    "categoria": "Trabalho"
                }
                """.formatted(LocalDateTime.now().plusDays(1).format(formatter));

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());

        // Depois busca por categoria
        mockMvc.perform(get("/api/tasks/search?categoria=Trabalho&page=0&size=10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable").exists());
    }

    @Test
    void deveMarcarTarefaComoConcluidaComSucesso() throws Exception {
        // Primeiro cria uma tarefa
        String json = """
                {
                    "titulo": "Tarefa para Concluir",
                    "descricao": "Descrição da tarefa",
                    "prioridade": "MEDIA",
                    "dataLimite": "%s",
                    "categoria": "Pessoal"
                }
                """.formatted(LocalDateTime.now().plusDays(1).format(formatter));

        String response = mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extrai o ID da resposta JSON
        String id = response.substring(response.indexOf("\"id\":") + 5, response.indexOf(","));
        id = id.trim();

        // Marca como concluída
        mockMvc.perform(patch("/api/tasks/{id}/concluir", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.concluida").value(true));
    }

    @Test
    void deveRetornarConflictAoExcluirTarefaConcluida() throws Exception {
        // Primeiro cria uma tarefa
        String json = """
                {
                    "titulo": "Tarefa para Excluir",
                    "descricao": "Descrição da tarefa",
                    "prioridade": "ALTA",
                    "dataLimite": "%s",
                    "categoria": "Trabalho"
                }
                """.formatted(LocalDateTime.now().plusDays(1).format(formatter));

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());

        String id = "1"; // Simulação do ID

        // Marca como concluída
        mockMvc.perform(patch("/api/tasks/{id}/concluir", id))
                .andExpect(status().isOk());

        // Tenta excluir tarefa concluída
        mockMvc.perform(delete("/api/tasks/{id}", id))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Estado inválido da tarefa"));
    }

    @Test
    void deveAtualizarTarefaComSucesso() throws Exception {
        // Primeiro cria uma tarefa
        String jsonCreate = """
                {
                    "titulo": "Tarefa Original",
                    "descricao": "Descrição original",
                    "prioridade": "BAIXA",
                    "dataLimite": "%s",
                    "categoria": "Trabalho"
                }
                """.formatted(LocalDateTime.now().plusDays(1).format(formatter));

        String response = mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCreate))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extrai o ID da resposta JSON
        String id = response.substring(response.indexOf("\"id\":") + 5, response.indexOf(","));
        id = id.trim();

        // Atualiza a tarefa
        String jsonUpdate = """
                {
                    "titulo": "Tarefa Atualizada",
                    "descricao": "Descrição atualizada",
                    "prioridade": "ALTA",
                    "dataLimite": "%s",
                    "categoria": "Estudo"
                }
                """.formatted(LocalDateTime.now().plusDays(2).format(formatter));

        mockMvc.perform(put("/api/tasks/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUpdate))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Tarefa Atualizada"))
                .andExpect(jsonPath("$.descricao").value("Descrição atualizada"))
                .andExpect(jsonPath("$.prioridade").value("ALTA"))
                .andExpect(jsonPath("$.categoria").value("Estudo"));
    }
}
