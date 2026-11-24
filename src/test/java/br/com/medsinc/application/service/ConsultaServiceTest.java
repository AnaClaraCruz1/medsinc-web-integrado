package br.com.medsinc.application.service;

import br.com.medsinc.api.dto.ConsultaCadastroRequest;
import br.com.medsinc.api.dto.ConsultaResponse;
import br.com.medsinc.infrastructure.repository.ConsultaJdbcRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaServiceTest {

    @Mock
    private ConsultaJdbcRepository repository;

    private ConsultaService service;

    @BeforeEach
    void setUp() {
        service = new ConsultaService(repository);
        lenient().when(repository.usuarioEhMedico(anyInt())).thenReturn(true);
    }

    @Test
    void deveListarConsultasDoRepositorio() {
        ConsultaResponse consulta = new ConsultaResponse(
                1, 2, "Paciente Teste", 3, "Medico Teste",
                LocalDate.of(2026, 12, 1), LocalTime.of(9, 30), "agendada");
        when(repository.listar("Paciente")).thenReturn(List.of(consulta));

        assertEquals(List.of(consulta), service.listar("Paciente"));
    }

    @Test
    void deveListarMedicosDisponiveis() {
        var medico = new ConsultaJdbcRepository.Profissional(3, "Medico Teste");
        when(repository.listarMedicos()).thenReturn(List.of(medico));

        assertEquals(List.of(medico), service.listarMedicos());
    }

    @Test
    void deveCadastrarConsultaComStatusPadraoAgendada() {
        var request = new ConsultaCadastroRequest(
                2, 3, LocalDate.of(2026, 12, 1), LocalTime.of(9, 30), null);

        service.cadastrar(request);

        ArgumentCaptor<String> status = ArgumentCaptor.forClass(String.class);
        verify(repository).inserir(eq(2), eq(3), any(), any(), status.capture());
        assertEquals("agendada", status.getValue());
    }

    @Test
    void deveNormalizarStatusInformado() {
        var request = new ConsultaCadastroRequest(
                2, 3, LocalDate.of(2026, 12, 1), LocalTime.of(9, 30), "REALIZADA");

        service.cadastrar(request);

        verify(repository).inserir(eq(2), eq(3), any(), any(), eq("realizada"));
    }

    @Test
    void deveRejeitarStatusInvalido() {
        var request = new ConsultaCadastroRequest(
                2, 3, LocalDate.of(2026, 12, 1), LocalTime.of(9, 30), "pendente");

        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(request));
        verifyNoInteractions(repository);
    }

    @Test
    void deveRejeitarUsuarioQueNaoSejaMedico() {
        when(repository.usuarioEhMedico(99)).thenReturn(false);
        var request = new ConsultaCadastroRequest(
                2, 99, LocalDate.of(2026, 12, 1), LocalTime.of(9, 30), "agendada");

        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(request));
        verify(repository, never()).inserir(anyInt(), anyInt(), any(), any(), anyString());
    }

    @Test
    void deveRejeitarDataAnteriorAoLimiteDoSistema() {
        var request = new ConsultaCadastroRequest(
                2, 3, LocalDate.of(1899, 12, 31), LocalTime.of(9, 30), "agendada");

        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(request));
        verifyNoInteractions(repository);
    }
}
