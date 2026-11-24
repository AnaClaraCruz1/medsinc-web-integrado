package br.com.medsinc.infrastructure.repository;

import br.com.medsinc.api.dto.PacienteResponse;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PacienteJdbcRepository {

    private static final String SELECT_BASE = """
            SELECT paciente_id, nome, cpf, data_nascimento,
                   CASE
                     WHEN data_nascimento IS NOT NULL
                     THEN TIMESTAMPDIFF(YEAR, data_nascimento, CURDATE())
                     ELSE idade
                   END AS idade_atual,
                   telefone, email, endereco, convenio
              FROM pacientes
            """;

    private final JdbcTemplate jdbcTemplate;

    public PacienteJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PacienteResponse> listar(String busca) {
        String termo = busca == null ? "" : busca.trim();
        if (termo.isEmpty()) {
            return jdbcTemplate.query(
                    SELECT_BASE + " ORDER BY nome",
                    (rs, rowNum) -> mapear(rs));
        }
        String filtro = "%" + termo + "%";
        String cpf = "%" + termo.replaceAll("\\D", "") + "%";
        return jdbcTemplate.query(
                SELECT_BASE + " WHERE nome LIKE ? OR REPLACE(REPLACE(REPLACE(cpf, '.', ''), '-', ''), ' ', '') LIKE ? ORDER BY nome",
                (rs, rowNum) -> mapear(rs), filtro, cpf);
    }

    public Optional<PacienteResponse> buscarPorId(int id) {
        List<PacienteResponse> encontrados = jdbcTemplate.query(
                SELECT_BASE + " WHERE paciente_id = ?",
                (rs, rowNum) -> mapear(rs), id);
        return encontrados.stream().findFirst();
    }

    private PacienteResponse mapear(java.sql.ResultSet rs) throws java.sql.SQLException {
        Date nascimento = rs.getDate("data_nascimento");
        int idade = rs.getInt("idade_atual");
        Integer idadeValor = rs.wasNull() ? null : idade;
        return new PacienteResponse(
                rs.getInt("paciente_id"),
                rs.getString("nome"),
                rs.getString("cpf"),
                nascimento == null ? null : nascimento.toLocalDate(),
                idadeValor,
                rs.getString("telefone"),
                rs.getString("email"),
                rs.getString("endereco"),
                rs.getString("convenio"));
    }
}
