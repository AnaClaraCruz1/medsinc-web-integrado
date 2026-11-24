package br.com.medsinc.api.dto;

import java.time.LocalDate;

public record PacienteResponse(
        Integer id,
        String nome,
        String cpf,
        LocalDate nascimento,
        Integer idade,
        String telefone,
        String email,
        String endereco,
        String convenio
) {}
