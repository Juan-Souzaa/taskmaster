package com.taskmaster.service;

import com.taskmaster.dto.TaskRequestDTO;
import com.taskmaster.dto.TaskResponseDTO;
import com.taskmaster.dto.TaskUpdateDTO;
import com.taskmaster.exception.InvalidTaskStateException;
import com.taskmaster.exception.ResourceNotFoundException;
import com.taskmaster.exception.ValidationException;
import com.taskmaster.model.Prioridade;
import com.taskmaster.model.Task;
import com.taskmaster.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceUnitTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TaskService taskService;

    private TaskRequestDTO taskRequestDTO;
    private TaskUpdateDTO taskUpdateDTO;
    private TaskResponseDTO taskResponseDTO;
    private Task task;

    @BeforeEach
    void setUp() {
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
        when(modelMapper.map(taskRequestDTO, Task.class)).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(modelMapper.map(task, TaskResponseDTO.class)).thenReturn(taskResponseDTO);

        // When
        TaskResponseDTO result = taskService.createTask(taskRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals(taskResponseDTO.getId(), result.getId());
        assertEquals(taskResponseDTO.getTitulo(), result.getTitulo());
        assertFalse(result.getConcluida());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void deveLancarExcecaoAoCriarTarefaComDataLimiteInvalida() {
        // Given
        taskRequestDTO.setDataLimite(LocalDateTime.now().minusDays(1));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
                () -> taskService.createTask(taskRequestDTO));
        assertEquals("Data limite não pode ser anterior à data atual", exception.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deveBuscarTarefaPorIdComSucesso() {
        // Given
        Long id = 1L;
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(modelMapper.map(task, TaskResponseDTO.class)).thenReturn(taskResponseDTO);

        // When
        TaskResponseDTO result = taskService.getTaskById(id);

        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(taskRepository, times(1)).findById(id);
    }

    @Test
    void deveLancarExcecaoAoBuscarTarefaInexistente() {
        // Given
        Long id = 999L;
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> taskService.getTaskById(id));
        assertEquals("Tarefa não encontrada: " + id, exception.getMessage());
    }

    @Test
    void deveListarTarefasComPaginacao() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> page = new PageImpl<>(List.of(task), pageable, 1);
        when(taskRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(task, TaskResponseDTO.class)).thenReturn(taskResponseDTO);

        // When
        Page<TaskResponseDTO> result = taskService.getAllTasks(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(taskRepository, times(1)).findAll(pageable);
    }

    @Test
    void deveBuscarTarefasPorCategoria() {
        // Given
        String categoria = "Trabalho";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> page = new PageImpl<>(List.of(task), pageable, 1);
        when(taskRepository.findByCategoriaContainingIgnoreCase(categoria, pageable)).thenReturn(page);
        when(modelMapper.map(task, TaskResponseDTO.class)).thenReturn(taskResponseDTO);

        // When
        Page<TaskResponseDTO> result = taskService.getTasksByCategoria(categoria, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(taskRepository, times(1)).findByCategoriaContainingIgnoreCase(categoria, pageable);
    }

    @Test
    void deveAtualizarTarefaComSucesso() {
        // Given
        Long id = 1L;
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(modelMapper.map(task, TaskResponseDTO.class)).thenReturn(taskResponseDTO);

        // When
        TaskResponseDTO result = taskService.updateTask(id, taskUpdateDTO);

        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(taskRepository, times(1)).findById(id);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarTarefaConcluida() {
        // Given
        Long id = 1L;
        task.setConcluida(true);
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        // When & Then
        InvalidTaskStateException exception = assertThrows(InvalidTaskStateException.class,
                () -> taskService.updateTask(id, taskUpdateDTO));
        assertEquals("Não é possível editar uma tarefa concluída", exception.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deveMarcarTarefaComoConcluidaComSucesso() {
        // Given
        Long id = 1L;
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(modelMapper.map(task, TaskResponseDTO.class)).thenReturn(taskResponseDTO);

        // When
        TaskResponseDTO result = taskService.markTaskAsCompleted(id);

        // Then
        assertNotNull(result);
        assertTrue(task.getConcluida());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void deveLancarExcecaoAoMarcarTarefaJaConcluida() {
        // Given
        Long id = 1L;
        task.setConcluida(true);
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        // When & Then
        InvalidTaskStateException exception = assertThrows(InvalidTaskStateException.class,
                () -> taskService.markTaskAsCompleted(id));
        assertEquals("Tarefa já está concluída", exception.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deveExcluirTarefaComSucesso() {
        // Given
        Long id = 1L;
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        // When
        taskService.deleteTask(id);

        // Then
        verify(taskRepository, times(1)).deleteById(id);
    }

    @Test
    void deveLancarExcecaoAoExcluirTarefaConcluida() {
        // Given
        Long id = 1L;
        task.setConcluida(true);
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        // When & Then
        InvalidTaskStateException exception = assertThrows(InvalidTaskStateException.class,
                () -> taskService.deleteTask(id));
        assertEquals("Não é possível excluir uma tarefa concluída", exception.getMessage());
        verify(taskRepository, never()).deleteById(id);
    }
}
