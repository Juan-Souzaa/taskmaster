package com.taskmaster.repository;

import com.taskmaster.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    /**
     * Busca tarefas por categoria com paginação
     */
    Page<Task> findByCategoriaContainingIgnoreCase(String categoria, Pageable pageable);
    
    /**
     * Busca tarefas concluídas com paginação
     */
    Page<Task> findByConcluidaTrue(Pageable pageable);
    
    /**
     * Busca tarefas pendentes com paginação
     */
    Page<Task> findByConcluidaFalse(Pageable pageable);
    
    /**
     * Verifica se uma tarefa existe e não está concluída
     */
    boolean existsByIdAndConcluidaFalse(Long id);
}
