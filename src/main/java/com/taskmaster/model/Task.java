package com.taskmaster.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Título é obrigatório")
    @Column(nullable = false, length = 100)
    private String titulo;
    
    @Column(length = 500)
    private String descricao;
    
    @NotNull(message = "Prioridade é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridade prioridade;
    
    @NotNull(message = "Data limite é obrigatória")
    @Column(nullable = false)
    private LocalDateTime dataLimite;
    
    @Column(nullable = false)
    private Boolean concluida = false;
    
    @NotBlank(message = "Categoria é obrigatória")
    @Column(nullable = false, length = 50)
    private String categoria;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime criadaEm;
    
    @PrePersist
    protected void onCreate() {
        criadaEm = LocalDateTime.now();
    }
}

