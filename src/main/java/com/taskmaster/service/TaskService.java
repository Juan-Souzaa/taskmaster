package com.taskmaster.service;

import com.taskmaster.dto.TaskRequestDTO;
import com.taskmaster.dto.TaskResponseDTO;
import com.taskmaster.dto.TaskUpdateDTO;
import com.taskmaster.exception.InvalidTaskStateException;
import com.taskmaster.exception.ResourceNotFoundException;
import com.taskmaster.exception.ValidationException;
import com.taskmaster.model.Task;
import com.taskmaster.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;
    
    /**
     * Cria uma nova tarefa
     */
    public TaskResponseDTO createTask(TaskRequestDTO requestDTO) {
        validateTaskData(requestDTO);
        
        Task task = modelMapper.map(requestDTO, Task.class);
        task.setConcluida(false);
        
        Task savedTask = taskRepository.save(task);
        return modelMapper.map(savedTask, TaskResponseDTO.class);
    }
    
    /**
     * Busca uma tarefa por ID
     */
    @Transactional(readOnly = true)
    public TaskResponseDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada: " + id));
        
        return modelMapper.map(task, TaskResponseDTO.class);
    }
    
    /**
     * Lista todas as tarefas com paginação
     */
    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> getAllTasks(Pageable pageable) {
        Page<Task> tasks = taskRepository.findAll(pageable);
        return tasks.map(task -> modelMapper.map(task, TaskResponseDTO.class));
    }
    
    /**
     * Busca tarefas por categoria
     */
    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> getTasksByCategoria(String categoria, Pageable pageable) {
        Page<Task> tasks = taskRepository.findByCategoriaContainingIgnoreCase(categoria, pageable);
        return tasks.map(task -> modelMapper.map(task, TaskResponseDTO.class));
    }
    
    /**
     * Atualiza uma tarefa existente
     */
    public TaskResponseDTO updateTask(Long id, TaskUpdateDTO updateDTO) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada: " + id));
        
        // Verifica se a tarefa não está concluída
        if (existingTask.getConcluida()) {
            throw new InvalidTaskStateException("Não é possível editar uma tarefa concluída");
        }
        
        validateTaskData(updateDTO);
        
        // Atualiza os campos
        existingTask.setTitulo(updateDTO.getTitulo());
        existingTask.setDescricao(updateDTO.getDescricao());
        existingTask.setPrioridade(updateDTO.getPrioridade());
        existingTask.setDataLimite(updateDTO.getDataLimite());
        existingTask.setCategoria(updateDTO.getCategoria());
        
        Task updatedTask = taskRepository.save(existingTask);
        return modelMapper.map(updatedTask, TaskResponseDTO.class);
    }
    
    /**
     * Marca uma tarefa como concluída
     */
    public TaskResponseDTO markTaskAsCompleted(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada: " + id));
        
        if (task.getConcluida()) {
            throw new InvalidTaskStateException("Tarefa já está concluída");
        }
        
        task.setConcluida(true);
        Task updatedTask = taskRepository.save(task);
        return modelMapper.map(updatedTask, TaskResponseDTO.class);
    }
    
    /**
     * Exclui uma tarefa
     */
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada: " + id));
        
        // Verifica se a tarefa não está concluída
        if (task.getConcluida()) {
            throw new InvalidTaskStateException("Não é possível excluir uma tarefa concluída");
        }
        
        taskRepository.deleteById(id);
    }
    
    /**
     * Valida os dados da tarefa
     */
    private void validateTaskData(Object dto) {
        LocalDateTime dataLimite = null;
        
        if (dto instanceof TaskRequestDTO) {
            dataLimite = ((TaskRequestDTO) dto).getDataLimite();
        } else if (dto instanceof TaskUpdateDTO) {
            dataLimite = ((TaskUpdateDTO) dto).getDataLimite();
        }
        
        if (dataLimite != null && dataLimite.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Data limite não pode ser anterior à data atual");
        }
    }
}
