package br.com.medsinc.infrastructure.repository;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class GestaoJdbcRepository {
    private final JdbcTemplate jdbc;

    public GestaoJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> listarEstoque() {
        return jdbc.queryForList("""
                SELECT item_id AS id, nome_item AS nome, quantidade,
                       unidade_medida AS unidade, quantidade_minima AS minimo,
                       atualizado_por AS atualizadoPor,
                       CASE WHEN quantidade <= quantidade_minima THEN 'baixo' ELSE 'normal' END AS situacao
                  FROM estoque ORDER BY nome_item
                """);
    }

    public void inserirEstoque(String nome, int quantidade, String unidade, int minimo, int usuarioId) {
        jdbc.update("""
                INSERT INTO estoque (nome_item, quantidade, unidade_medida, quantidade_minima, atualizado_por)
                VALUES (?, ?, ?, ?, ?)
                """, nome, quantidade, unidade, minimo, usuarioId);
    }

    public void atualizarEstoque(int id, String nome, int quantidade, String unidade, int minimo, int usuarioId) {
        int alterados = jdbc.update("""
                UPDATE estoque SET nome_item=?, quantidade=?, unidade_medida=?, quantidade_minima=?, atualizado_por=?
                 WHERE item_id=?
                """, nome, quantidade, unidade, minimo, usuarioId, id);
        if (alterados == 0) throw new IllegalArgumentException("Item de estoque não encontrado.");
    }

    public void excluirEstoque(int id) {
        int alterados = jdbc.update("DELETE FROM estoque WHERE item_id=?", id);
        if (alterados == 0) throw new IllegalArgumentException("Item de estoque não encontrado.");
    }

    public List<Map<String, Object>> listarHistoricos(String busca) {
        String filtro = "%" + (busca == null ? "" : busca.trim()) + "%";
        return jdbc.queryForList("""
                SELECT h.historico_id AS id, h.paciente_id AS pacienteId, p.nome AS pacienteNome,
                       h.medico_id AS medicoId, u.nome AS medicoNome,
                       h.descricao, h.data_registro AS data
                  FROM historicos_medicos h
                  JOIN pacientes p ON p.paciente_id=h.paciente_id
                  JOIN usuarios u ON u.usuario_id=h.medico_id
                 WHERE p.nome LIKE ? OR u.nome LIKE ? OR h.descricao LIKE ?
                 ORDER BY h.data_registro DESC, h.historico_id DESC
                """, filtro, filtro, filtro);
    }

    public void inserirHistorico(int pacienteId, int medicoId, String descricao, Date data) {
        jdbc.update("""
                INSERT INTO historicos_medicos (paciente_id, medico_id, descricao, data_registro)
                VALUES (?, ?, ?, ?)
                """, pacienteId, medicoId, descricao, data);
    }

    public List<Map<String, Object>> listarUsuarios() {
        return jdbc.queryForList("""
                SELECT usuario_id AS id, nome, email, perfil, created_at AS criadoEm
                  FROM usuarios ORDER BY nome
                """);
    }

    public void inserirUsuario(String nome, String email, String senhaHash, String perfil) {
        jdbc.update("INSERT INTO usuarios (nome, email, senha, perfil) VALUES (?, ?, ?, ?)",
                nome, email, senhaHash, perfil);
    }

    public Map<String, Object> resumoRelatorios() {
        return jdbc.queryForMap("""
                SELECT
                  (SELECT COUNT(*) FROM pacientes) AS pacientes,
                  (SELECT COUNT(*) FROM consultas) AS consultas,
                  (SELECT COUNT(*) FROM estoque) AS itensEstoque,
                  (SELECT COUNT(*) FROM estoque WHERE quantidade <= quantidade_minima) AS estoqueBaixo,
                  (SELECT COUNT(*) FROM historicos_medicos) AS historicos,
                  (SELECT COUNT(*) FROM usuarios) AS usuarios
                """);
    }

    public List<Map<String, Object>> listarRelatorios() {
        return jdbc.queryForList("""
                SELECT r.relatorio_id AS id, r.tipo, r.descricao,
                       r.gerado_por AS geradoPor, u.nome AS responsavel,
                       r.data_geracao AS data
                  FROM relatorios r JOIN usuarios u ON u.usuario_id=r.gerado_por
                 ORDER BY r.data_geracao DESC, r.relatorio_id DESC
                """);
    }

    public void inserirRelatorio(String tipo, String descricao, int usuarioId) {
        jdbc.update("INSERT INTO relatorios (tipo, descricao, gerado_por, data_geracao) VALUES (?, ?, ?, ?)",
                tipo, descricao, usuarioId, Date.valueOf(java.time.LocalDate.now()));
    }

    public List<Map<String, Object>> listarBackups() {
        return jdbc.queryForList("""
                SELECT b.backup_id AS id, b.caminho_arquivo AS arquivo,
                       b.data_backup AS data, b.realizado_por AS realizadoPor,
                       u.nome AS responsavel
                  FROM backups b JOIN usuarios u ON u.usuario_id=b.realizado_por
                 ORDER BY b.data_backup DESC
                """);
    }

    public void inserirBackup(String caminho, int usuarioId) {
        jdbc.update("INSERT INTO backups (caminho_arquivo, data_backup, realizado_por) VALUES (?, CURRENT_TIMESTAMP, ?)",
                caminho, usuarioId);
    }
}
