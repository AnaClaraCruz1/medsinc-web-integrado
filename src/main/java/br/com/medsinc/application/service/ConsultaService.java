package br.com.medsinc.application.service;

import br.com.medsinc.api.dto.ConsultaCadastroRequest;
import br.com.medsinc.api.dto.ConsultaResponse;
import br.com.medsinc.infrastructure.repository.ConsultaJdbcRepository;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsultaService {

    private final ConsultaJdbcRepository repository;

    public ConsultaService(ConsultaJdbcRepository repository) {
        this.repository = repository;
    }

    public List<ConsultaResponse> listar(String busca) {
        return List.copyOf(repository.listar(busca));
    }

    public List<ConsultaJdbcRepository.Profissional> listarMedicos() {
        return List.copyOf(repository.listarMedicos());
    }

    @Transactional
    public void cadastrar(ConsultaCadastroRequest dados) {
        if (dados.data().isBefore(LocalDate.of(1900, 1, 1))) {
            throw new IllegalArgumentException("A data da consulta é inválida.");
        }
        if (!repository.usuarioEhMedico(dados.medicoId())) {\n            throw new IllegalArgumentException("O profissional selecionado não possui perfil médico.");\n        }\n        String status = dados.status() == null || dados.status().isBlank()
                ? "agendada"
                : dados.status().trim().toLowerCase(Locale.ROOT);
        if (!List.of("agendada", "realizada", "cancelada").contains(status)) {
            throw new IllegalArgumentException("Status de consulta inválido.");
        }
        repository.inserir(
                dados.pacienteId(),
                dados.medicoId(),
                Date.valueOf(dados.data()),
                Time.valueOf(dados.hora()),
                status);
    }
}
