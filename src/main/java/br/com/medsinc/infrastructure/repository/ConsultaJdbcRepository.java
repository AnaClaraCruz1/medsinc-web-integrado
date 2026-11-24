package br.com.medsinc.infrastructure.repository;

import br.com.medsinc.api.dto.ConsultaResponse;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ConsultaJdbcRepository {

    private static final String SELECT_BASE = """
            SELECT c.consulta_id, c.paciente_id, p.nome AS paciente_nome,
                   c.medico_id, u.nome AS medico_nome,
                   c.data_consulta, c.hora_consulta, c.status
              FROM consultas c
              JOIN pacientes p ON p.paciente_id = c.paciente_id
              JOIN usuarios u ON u.usuario_id = c.medico_id
            """;

    private static final RowMapper<ConsultaResponse> MAPPER = (rs, rowNum) ->
            new ConsultaResponse(
                    rs.getInt("consulta_id"),
                    rs.getInt("paciente_id"),
                    rs.getString("paciente_nome"),
                    rs.getInt("medico_id"),
                    rs.getString("medico_nome"),
                    rs.getDate("data_consulta").toLocalDate(),
                    rs.getTime("hora_consulta").toLocalTime(),
                    rs.getString("status"));

    private final JdbcTemplate jdbcTemplate;

    public ConsultaJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ConsultaResponse> listar(String busca) {
        String termo = busca == null ? "" : busca.trim();
        if (termo.isEmpty()) {
            return jdbcTemplate.query(SELECT_BASE + " ORDER BY c.data_consulta, c.hora_consulta", MAPPER);
        }
        String filtro = "%" + termo + "%";
        return jdbcTemplate.query(
                SELECT_BASE + " WHERE p.nome LIKE ? OR u.nome LIKE ? OR c.status LIKE ? ORDER BY c.data_consulta, c.hora_consulta",
                MAPPER, filtro, filtro, filtro);
    }

    public int inserir(int pacienteId, int medicoId, Date data, Time hora, String status) {
        return jdbcTemplate.update(
                "INSERT INTO consultas (paciente_id, medico_id, data_consulta, hora_consulta, status) VALUES (?, ?, ?, ?, ?)",
                pacienteId, medicoId, data, hora, status);
    }

    public boolean usuarioEhMedico(int usuarioId) {
        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM usuarios WHERE usuario_id = ? AND perfil = 'medico'",
                Integer.class, usuarioId);
        return total != null && total > 0;
    }

    public List<Profissional> listarMedicos() {
        return jdbcTemplate.query(
                "SELECT usuario_id, nome FROM usuarios WHERE perfil = 'medico' ORDER BY nome",
                (rs, rowNum) -> new Profissional(rs.getInt("usuario_id"), rs.getString("nome")));
    }

    public record Profissional(Integer id, String nome) {
    }
}
