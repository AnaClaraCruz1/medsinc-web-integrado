package br.com.medsinc.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalTime;

public record ConsultaCadastroRequest(
        @NotNull @Positive Integer pacienteId,
        @NotNull @Positive Integer medicoId,
        @NotNull LocalDate data,
        @NotNull LocalTime hora,
        String status) {
}
