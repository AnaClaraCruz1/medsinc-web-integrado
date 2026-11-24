package br.com.medsinc.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record Consulta(Integer id, int pacienteId, int medicoId, LocalDate data, LocalTime hora, StatusConsulta status) {
    public enum StatusConsulta { AGENDADA, REALIZADA, CANCELADA }
    public Consulta {
        if (pacienteId <= 0 || medicoId <= 0) throw new IllegalArgumentException("Paciente e médico devem ser informados.");
        if (data == null || hora == null || status == null) throw new IllegalArgumentException("Data, horário e status são obrigatórios.");
    }
}
