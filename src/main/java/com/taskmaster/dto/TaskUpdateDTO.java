package com.taskmaster.dto;

import com.taskmaster.model.Prioridade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateDTO {
    
    @NotBlank(message = "Título é obrigatório")
    private String titulo;
    
    private String descricao;
    
    @NotNull(message = "Prioridade é obrigatória")
    private Prioridade prioridade;
    
    @NotNull(message = "Data limite é obrigatória")
    @Future(message = "Data limite deve ser futura")
    private LocalDateTime dataLimite;
    
    @NotBlank(message = "Categoria é obrigatória")
    private String categoria;
}



