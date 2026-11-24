package br.com.medsinc.domain.model;

import java.time.LocalDate;

public record HistoricoMedico(Integer id, int pacienteId, int medicoId, String descricao, LocalDate dataRegistro) {
    public HistoricoMedico {
        if (pacienteId <= 0 || medicoId <= 0) throw new IllegalArgumentException("Paciente e médico devem ser informados.");
        if (descricao == null || descricao.isBlank()) throw new IllegalArgumentException("A descrição do histórico é obrigatória.");
        if (dataRegistro == null) throw new IllegalArgumentException("A data do registro é obrigatória.");
    }
}
