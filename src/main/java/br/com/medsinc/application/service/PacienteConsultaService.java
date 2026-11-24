package br.com.medsinc.application.service;

import br.com.medsinc.api.dto.PacienteResponse;
import br.com.medsinc.infrastructure.repository.PacienteJdbcRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PacienteConsultaService {

    private final PacienteJdbcRepository repository;

    public PacienteConsultaService(PacienteJdbcRepository repository) {
        this.repository = repository;
    }

    public List<PacienteResponse> listar(String busca) {
        return List.copyOf(repository.listar(busca));
    }

    public PacienteResponse buscarPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Identificador de paciente inválido.");
        }
        return repository.buscarPorId(id)
                .orElseThrow(() -> new PacienteNaoEncontradoException(id));
    }

    public static class PacienteNaoEncontradoException extends RuntimeException {
        public PacienteNaoEncontradoException(int id) {
            super("Paciente não encontrado: " + id);
        }
    }
}
