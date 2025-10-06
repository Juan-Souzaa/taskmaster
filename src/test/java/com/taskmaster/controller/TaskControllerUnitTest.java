package com.taskmaster.controller;

import com.taskmaster.dto.TaskRequestDTO;
import com.taskmaster.dto.TaskResponseDTO;
import com.taskmaster.dto.TaskUpdateDTO;
import com.taskmaster.exception.InvalidTaskStateException;
import com.taskmaster.exception.ResourceNotFoundException;
import com.taskmaster.model.Prioridade;
import com.taskmaster.model.Task;
import com.taskmaster.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerUnitTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private TaskRequestDTO taskRequestDTO;
    private TaskUpdateDTO taskUpdateDTO;
    private TaskResponseDTO taskResponseDTO;
    private Task task;

    @BeforeEach
    void setUp() {
        // Configuração dos DTOs de teste
        taskRequestDTO = new TaskRequestDTO();
        taskRequestDTO.setTitulo("Tarefa de Teste");
        taskRequestDTO.setDescricao("Descrição da tarefa");
        taskRequestDTO.setPrioridade(Prioridade.MEDIA);
        taskRequestDTO.setDataLimite(LocalDateTime.now().plusDays(1));
        taskRequestDTO.setCategoria("Trabalho");

        taskUpdateDTO = new TaskUpdateDTO();
        taskUpdateDTO.setTitulo("Tarefa Atualizada");
        taskUpdateDTO.setDescricao("Descrição atualizada");
        taskUpdateDTO.setPrioridade(Prioridade.ALTA);
        taskUpdateDTO.setDataLimite(LocalDateTime.now().plusDays(2));
        taskUpdateDTO.setCategoria("Estudo");

        taskResponseDTO = new TaskResponseDTO();
        taskResponseDTO.setId(1L);
        taskResponseDTO.setTitulo("Tarefa de Teste");
        taskResponseDTO.setDescricao("Descrição da tarefa");
        taskResponseDTO.setPrioridade(Prioridade.MEDIA);
        taskResponseDTO.setDataLimite(LocalDateTime.now().plusDays(1));
        taskResponseDTO.setConcluida(false);
        taskResponseDTO.setCategoria("Trabalho");
        taskResponseDTO.setCriadaEm(LocalDateTime.now());

        task = new Task();
        task.setId(1L);
        task.setTitulo("Tarefa de Teste");
        task.setDescricao("Descrição da tarefa");
        task.setPrioridade(Prioridade.MEDIA);
        task.setDataLimite(LocalDateTime.now().plusDays(1));
        task.setConcluida(false);
        task.setCategoria("Trabalho");
        task.setCriadaEm(LocalDateTime.now());
    }

    @Test
    void deveCriarTarefaComSucesso() {
        // Given
        when(taskService.createTask(any(TaskRequestDTO.class))).thenReturn(taskResponseDTO);

        // When
        ResponseEntity<TaskResponseDTO> response = taskController.createTask(taskRequestDTO);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(taskResponseDTO.getId(), response.getBody().getId());
        assertEquals(taskResponseDTO.getTitulo(), response.getBody().getTitulo());
        verify(taskService, times(1)).createTask(taskRequestDTO);
    }

    @Test
    void deveBuscarTarefaPorIdComSucesso() {
        // Given
        Long id = 1L;
        when(taskService.getTaskById(id)).thenReturn(taskResponseDTO);

        // When
        ResponseEntity<TaskResponseDTO> response = taskController.getTaskById(id);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getId());
        verify(taskService, times(1)).getTaskById(id);
    }

    @Test
    void deveRetornarNotFoundQuandoTarefaNaoExiste() {
        // Given
        Long id = 999L;
        when(taskService.getTaskById(id)).thenThrow(new ResourceNotFoundException("Tarefa não encontrada: " + id));

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> taskController.getTaskById(id));
        verify(taskService, times(1)).getTaskById(id);
    }

    @Test
    void deveListarTarefasComPaginacao() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<TaskResponseDTO> page = new PageImpl<>(List.of(taskResponseDTO), pageable, 1);
        when(taskService.getAllTasks(pageable)).thenReturn(page);

        // When
        ResponseEntity<Page<TaskResponseDTO>> response = taskController.getAllTasks(pageable);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        verify(taskService, times(1)).getAllTasks(pageable);
    }

    @Test
    void deveBuscarTarefasPorCategoria() {
        // Given
        String categoria = "Trabalho";
        Pageable pageable = PageRequest.of(0, 10);
        Page<TaskResponseDTO> page = new PageImpl<>(List.of(taskResponseDTO), pageable, 1);
        when(taskService.getTasksByCategoria(categoria, pageable)).thenReturn(page);

        // When
        ResponseEntity<Page<TaskResponseDTO>> response = taskController.getTasksByCategoria(categoria, pageable);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        verify(taskService, times(1)).getTasksByCategoria(categoria, pageable);
    }

    @Test
    void deveAtualizarTarefaComSucesso() {
        // Given
        Long id = 1L;
        when(taskService.updateTask(eq(id), any(TaskUpdateDTO.class))).thenReturn(taskResponseDTO);

        // When
        ResponseEntity<TaskResponseDTO> response = taskController.updateTask(id, taskUpdateDTO);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getId());
        verify(taskService, times(1)).updateTask(id, taskUpdateDTO);
    }

    @Test
    void deveRetornarConflictAoAtualizarTarefaConcluida() {
        // Given
        Long id = 1L;
        when(taskService.updateTask(eq(id), any(TaskUpdateDTO.class)))
                .thenThrow(new InvalidTaskStateException("Não é possível editar uma tarefa concluída"));

        // When & Then
        assertThrows(InvalidTaskStateException.class, () -> taskController.updateTask(id, taskUpdateDTO));
        verify(taskService, times(1)).updateTask(id, taskUpdateDTO);
    }

    @Test
    void deveMarcarTarefaComoConcluidaComSucesso() {
        // Given
        Long id = 1L;
        taskResponseDTO.setConcluida(true);
        when(taskService.markTaskAsCompleted(id)).thenReturn(taskResponseDTO);

        // When
        ResponseEntity<TaskResponseDTO> response = taskController.markTaskAsCompleted(id);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getConcluida());
        verify(taskService, times(1)).markTaskAsCompleted(id);
    }

    @Test
    void deveExcluirTarefaComSucesso() {
        // Given
        Long id = 1L;
        doNothing().when(taskService).deleteTask(id);

        // When
        ResponseEntity<Void> response = taskController.deleteTask(id);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(taskService, times(1)).deleteTask(id);
    }

    @Test
    void deveRetornarConflictAoExcluirTarefaConcluida() {
        // Given
        Long id = 1L;
        doThrow(new InvalidTaskStateException("Não é possível excluir uma tarefa concluída"))
                .when(taskService).deleteTask(id);

        // When & Then
        assertThrows(InvalidTaskStateException.class, () -> taskController.deleteTask(id));
        verify(taskService, times(1)).deleteTask(id);
    }
}
