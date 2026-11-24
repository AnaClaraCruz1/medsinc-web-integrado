package br.com.medsinc.api.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ConsultaResponse(
        Integer id,
        Integer pacienteId,
        String pacienteNome,
        Integer medicoId,
        String medicoNome,
        LocalDate data,
        LocalTime hora,
        String status) {
}
