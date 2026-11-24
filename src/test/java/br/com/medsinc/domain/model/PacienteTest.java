package br.com.medsinc.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PacienteTest {

    @Test
    void deveCriarPacienteComDadosValidos() {
        Paciente paciente = new Paciente(null, "Maria", 32, "Rua A", "99999-0000", 1);

        assertEquals("Maria", paciente.nome());
        assertEquals(32, paciente.idade());
    }

    @Test
    void deveRejeitarNomeAusente() {
        assertThrows(IllegalArgumentException.class,
                () -> new Paciente(null, " ", 32, "Rua A", "99999-0000", 1));
    }

    @Test
    void deveRejeitarIdadeNegativa() {
        assertThrows(IllegalArgumentException.class,
                () -> new Paciente(null, "Maria", -1, "Rua A", "99999-0000", 1));
    }

    @Test
    void deveExigirUsuarioResponsavelPeloCadastro() {
        assertThrows(IllegalArgumentException.class,
                () -> new Paciente(null, "Maria", 32, "Rua A", "99999-0000", 0));
    }

    @Test
    void deveGerarCopiaSemIdentificador() {
        Paciente paciente = new Paciente(10, "Maria", 32, "Rua A", "99999-0000", 1);

        Paciente novo = paciente.semId();

        assertNull(novo.id());
        assertEquals(paciente.nome(), novo.nome());
        assertEquals(paciente.idade(), novo.idade());
    }
}
