package br.com.medsinc.application.service;

import br.com.medsinc.api.dto.PacienteCadastroRequest;
import br.com.medsinc.api.dto.PacienteResponse;
import br.com.medsinc.infrastructure.repository.PacienteJdbcRepository;
import java.time.LocalDate;
import java.time.Period;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PacienteCadastroService {

    private final PacienteJdbcRepository repository;

    public PacienteCadastroService(PacienteJdbcRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PacienteResponse cadastrar(PacienteCadastroRequest request, Integer usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("Usuário autenticado inválido.");
        }
        String cpf = request.cpf().replaceAll("\\D", "");
        if (cpf.length() != 11) {
            throw new IllegalArgumentException("Informe um CPF com 11 dígitos.");
        }
        LocalDate nascimento = request.nascimento();
        if (nascimento == null || nascimento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data de nascimento não pode ser futura.");
        }
        int idade = Period.between(nascimento, LocalDate.now()).getYears();
        int id = repository.inserir(
                request.nome().trim(),
                cpf,
                nascimento,
                idade,
                request.telefone().trim(),
                request.email() == null || request.email().isBlank() ? null : request.email().trim(),
                request.endereco().trim(),
                request.convenio().trim(),
                usuarioId);
        return repository.buscarPorId(id)
                .orElseThrow(() -> new IllegalStateException("O paciente foi salvo, mas não pôde ser recuperado."));
    }
}
