package br.com.medsinc.application.service;

import br.com.medsinc.api.dto.PacienteResponse;
import br.com.medsinc.infrastructure.repository.PacienteJdbcRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PacienteConsultaServiceTest {

    @Test
    void deveListarPacientesPeloRepositorio() {
        PacienteJdbcRepository repository = mock(PacienteJdbcRepository.class);
        PacienteResponse paciente = new PacienteResponse(
                7, "Paciente Teste", "123.456.789-00",
                LocalDate.of(1990, 1, 10), 36,
                "(31)99999-0000", null, "Rua Teste, 10", "Particular");
        when(repository.listar("Teste")).thenReturn(List.of(paciente));

        PacienteConsultaService service = new PacienteConsultaService(repository);

        assertEquals(List.of(paciente), service.listar("Teste"));
        verify(repository).listar("Teste");
    }

    @Test
    void deveBuscarPacienteExistentePorId() {
        PacienteJdbcRepository repository = mock(PacienteJdbcRepository.class);
        PacienteResponse paciente = new PacienteResponse(
                7, "Paciente Teste", null, null, 30,
                null, null, "Rua Teste, 10", null);
        when(repository.buscarPorId(7)).thenReturn(Optional.of(paciente));

        PacienteConsultaService service = new PacienteConsultaService(repository);

        assertEquals(paciente, service.buscarPorId(7));
    }

    @Test
    void deveFalharQuandoPacienteNaoExiste() {
        PacienteJdbcRepository repository = mock(PacienteJdbcRepository.class);
        when(repository.buscarPorId(999)).thenReturn(Optional.empty());

        PacienteConsultaService service = new PacienteConsultaService(repository);

        assertThrows(PacienteConsultaService.PacienteNaoEncontradoException.class,
                () -> service.buscarPorId(999));
    }

    @Test
    void deveRejeitarIdentificadorInvalido() {
        PacienteJdbcRepository repository = mock(PacienteJdbcRepository.class);
        PacienteConsultaService service = new PacienteConsultaService(repository);

        assertThrows(IllegalArgumentException.class, () -> service.buscarPorId(0));
        verifyNoInteractions(repository);
    }
}
