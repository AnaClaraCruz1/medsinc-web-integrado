package br.com.medsinc.infrastructure.repository;

import br.com.medsinc.api.dto.PacienteResponse;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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
        String somenteDigitos = termo.replaceAll("\\D", "");
        if (somenteDigitos.isEmpty()) {
            return jdbcTemplate.query(
                    SELECT_BASE + " WHERE nome LIKE ? ORDER BY nome",
                    (rs, rowNum) -> mapear(rs), filtro);
        }
        String cpf = "%" + somenteDigitos + "%";
        return jdbcTemplate.query(
                SELECT_BASE + " WHERE CAST(paciente_id AS CHAR) LIKE ? OR nome LIKE ? OR REPLACE(REPLACE(REPLACE(cpf, '.', ''), '-', ''), ' ', '') LIKE ? ORDER BY nome",
                (rs, rowNum) -> mapear(rs), filtro, filtro, cpf);
    }

    public Optional<PacienteResponse> buscarPorId(int id) {
        List<PacienteResponse> encontrados = jdbcTemplate.query(
                SELECT_BASE + " WHERE paciente_id = ?",
                (rs, rowNum) -> mapear(rs), id);
        return encontrados.stream().findFirst();
    }

    public int inserir(
            String nome, String cpf, LocalDate nascimento, int idade,
            String telefone, String email, String endereco, String convenio,
            int usuarioCadastroId) {

        String sql = """
                INSERT INTO pacientes
                    (nome, cpf, data_nascimento, idade, endereco, contato,
                     telefone, email, convenio, usuario_cadastro_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, nome);
            statement.setString(2, cpf);
            statement.setDate(3, Date.valueOf(nascimento));
            statement.setInt(4, idade);
            statement.setString(5, endereco);
            // Compatibilidade com a Etapa 6: a coluna legada "contato" era usada
            // como contato principal. Mantemos o telefone nela e também na nova
            // coluna "telefone", sem alterar a leitura dos registros antigos.
            statement.setString(6, telefone);
            statement.setString(7, telefone);
            statement.setString(8, email);
            statement.setString(9, convenio);
            statement.setInt(10, usuarioCadastroId);
            return statement;
        }, keyHolder);

        Number chave = keyHolder.getKey();
        if (chave == null) {
            throw new IllegalStateException("O banco não retornou o identificador do paciente.");
        }
        return chave.intValue();
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
