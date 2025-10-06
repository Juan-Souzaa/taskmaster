package com.taskmaster.controller;

import com.taskmaster.dto.TaskRequestDTO;
import com.taskmaster.dto.TaskResponseDTO;
import com.taskmaster.dto.TaskUpdateDTO;
import com.taskmaster.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Tarefas", description = "API para gerenciamento de tarefas")
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    
    private final TaskService taskService;
    
    /**
     * Cria uma nova tarefa
     */
    @Operation(summary = "Criar nova tarefa", description = "Cria uma nova tarefa com os dados fornecidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO requestDTO) {
        TaskResponseDTO responseDTO = taskService.createTask(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    
    /**
     * Lista todas as tarefas com paginação
     */
    @Operation(summary = "Listar todas as tarefas", description = "Retorna uma lista paginada de todas as tarefas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefas encontradas com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @GetMapping
    public ResponseEntity<Page<TaskResponseDTO>> getAllTasks(Pageable pageable) {
        Page<TaskResponseDTO> tasks = taskService.getAllTasks(pageable);
        return ResponseEntity.ok(tasks);
    }
    
    /**
     * Busca uma tarefa por ID
     */
    @Operation(summary = "Buscar tarefa por ID", description = "Retorna uma tarefa específica com base no ID fornecido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id) {
        TaskResponseDTO responseDTO = taskService.getTaskById(id);
        return ResponseEntity.ok(responseDTO);
    }
    
    /**
     * Busca tarefas por categoria
     */
    @Operation(summary = "Buscar tarefas por categoria", description = "Retorna uma lista paginada de tarefas filtradas por categoria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<TaskResponseDTO>> getTasksByCategoria(
            @RequestParam String categoria,
            Pageable pageable) {
        Page<TaskResponseDTO> tasks = taskService.getTasksByCategoria(categoria, pageable);
        return ResponseEntity.ok(tasks);
    }
    
    /**
     * Marca uma tarefa como concluída
     */
    @Operation(summary = "Marcar tarefa como concluída", description = "Marca uma tarefa específica como concluída")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa marcada como concluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada"),
            @ApiResponse(responseCode = "409", description = "Tarefa já está concluída"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PatchMapping("/{id}/concluir")
    public ResponseEntity<TaskResponseDTO> markTaskAsCompleted(@PathVariable Long id) {
        TaskResponseDTO responseDTO = taskService.markTaskAsCompleted(id);
        return ResponseEntity.ok(responseDTO);
    }
    
    /**
     * Atualiza uma tarefa existente
     */
    @Operation(summary = "Atualizar tarefa", description = "Atualiza todos os dados de uma tarefa existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada"),
            @ApiResponse(responseCode = "409", description = "Tarefa concluída não pode ser editada"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateDTO updateDTO) {
        TaskResponseDTO responseDTO = taskService.updateTask(id, updateDTO);
        return ResponseEntity.ok(responseDTO);
    }
    
    /**
     * Exclui uma tarefa
     */
    @Operation(summary = "Excluir tarefa", description = "Remove permanentemente uma tarefa do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarefa excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada"),
            @ApiResponse(responseCode = "409", description = "Tarefa concluída não pode ser excluída"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}



