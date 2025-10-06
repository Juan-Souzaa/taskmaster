package com.taskmaster.dto;

import com.taskmaster.model.Prioridade;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDTO {
    
    private Long id;
    private String titulo;
    private String descricao;
    private Prioridade prioridade;
    private LocalDateTime dataLimite;
    private Boolean concluida;
    private String categoria;
    private LocalDateTime criadaEm;
}



