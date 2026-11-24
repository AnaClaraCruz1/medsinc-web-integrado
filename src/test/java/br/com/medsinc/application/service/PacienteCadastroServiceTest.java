package br.com.medsinc.application.service;

import br.com.medsinc.api.dto.PacienteCadastroRequest;
import br.com.medsinc.api.dto.PacienteResponse;
import br.com.medsinc.infrastructure.repository.PacienteJdbcRepository;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteCadastroServiceTest {

    @Mock
    private PacienteJdbcRepository repository;

    @InjectMocks
    private PacienteCadastroService service;

    @Test
    void deveNormalizarCpfCalcularIdadeEUsarUsuarioAutenticado() {
        LocalDate nascimento = LocalDate.of(1990, 5, 10);
        int idadeEsperada = Period.between(nascimento, LocalDate.now()).getYears();
        PacienteCadastroRequest request = new PacienteCadastroRequest(
                "  Paciente Teste  ", "123.456.789-00", nascimento,
                "(31)99999-1111", "teste@example.com",
                "Rua de Teste, 10", "Particular");
        PacienteResponse response = new PacienteResponse(
                42, "Paciente Teste", "12345678900", nascimento, idadeEsperada,
                "(31)99999-1111", "teste@example.com", "Rua de Teste, 10", "Particular");

        when(repository.inserir(
                "Paciente Teste", "12345678900", nascimento, idadeEsperada,
                "(31)99999-1111", "teste@example.com", "Rua de Teste, 10",
                "Particular", 9)).thenReturn(42);
        when(repository.buscarPorId(42)).thenReturn(Optional.of(response));

        PacienteResponse criado = service.cadastrar(request, 9);

        assertEquals(42, criado.id());
        verify(repository).inserir(
                "Paciente Teste", "12345678900", nascimento, idadeEsperada,
                "(31)99999-1111", "teste@example.com", "Rua de Teste, 10",
                "Particular", 9);
    }

    @Test
    void deveRejeitarCpfComQuantidadeIncorretaDeDigitos() {
        PacienteCadastroRequest request = new PacienteCadastroRequest(
                "Paciente Teste", "123.456", LocalDate.of(1990, 5, 10),
                "(31)99999-1111", null, "Rua de Teste", "Particular");

        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(request, 9));
        verifyNoInteractions(repository);
    }

    @Test
    void deveRejeitarDataDeNascimentoFutura() {
        PacienteCadastroRequest request = new PacienteCadastroRequest(
                "Paciente Teste", "12345678900", LocalDate.now().plusDays(1),
                "(31)99999-1111", null, "Rua de Teste", "Particular");

        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(request, 9));
        verifyNoInteractions(repository);
    }

    @Test
    void deveExigirUsuarioResponsavel() {
        PacienteCadastroRequest request = new PacienteCadastroRequest(
                "Paciente Teste", "12345678900", LocalDate.of(1990, 5, 10),
                "(31)99999-1111", null, "Rua de Teste", "Particular");

        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(request, null));
        verifyNoInteractions(repository);
    }
}
