package br.com.medsinc.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConsultaTest {

    @Test
    void deveCriarConsultaComDadosValidos() {
        Consulta consulta = new Consulta(null, 1, 2, LocalDate.of(2026, 12, 10),
                LocalTime.of(9, 30), Consulta.StatusConsulta.AGENDADA);

        assertEquals(1, consulta.pacienteId());
        assertEquals(2, consulta.medicoId());
        assertEquals(Consulta.StatusConsulta.AGENDADA, consulta.status());
    }

    @Test
    void deveRejeitarPacienteInvalido() {
        assertThrows(IllegalArgumentException.class,
                () -> new Consulta(null, 0, 2, LocalDate.now(),
                        LocalTime.NOON, Consulta.StatusConsulta.AGENDADA));
    }

    @Test
    void deveRejeitarMedicoInvalido() {
        assertThrows(IllegalArgumentException.class,
                () -> new Consulta(null, 1, 0, LocalDate.now(),
                        LocalTime.NOON, Consulta.StatusConsulta.AGENDADA));
    }

    @Test
    void deveExigirDataHorarioEStatus() {
        assertThrows(IllegalArgumentException.class,
                () -> new Consulta(null, 1, 2, null,
                        LocalTime.NOON, Consulta.StatusConsulta.AGENDADA));
        assertThrows(IllegalArgumentException.class,
                () -> new Consulta(null, 1, 2, LocalDate.now(),
                        null, Consulta.StatusConsulta.AGENDADA));
        assertThrows(IllegalArgumentException.class,
                () -> new Consulta(null, 1, 2, LocalDate.now(),
                        LocalTime.NOON, null));
    }
}
