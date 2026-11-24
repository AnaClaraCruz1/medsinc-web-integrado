package br.com.medsinc.infrastructure.repository;

import br.com.medsinc.application.service.UsuarioAutenticacao;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioAutenticacaoJdbcRepository implements UsuarioAutenticacaoRepository {

    private final JdbcTemplate jdbcTemplate;

    public UsuarioAutenticacaoJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<UsuarioAutenticacao> buscarPorEmail(String email) {
        return jdbcTemplate.query(
                """
                SELECT usuario_id, nome, email, senha, perfil
                  FROM usuarios
                 WHERE LOWER(email) = LOWER(?)
                """,
                (rs, rowNum) -> new UsuarioAutenticacao(
                        rs.getInt("usuario_id"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("senha"),
                        rs.getString("perfil")),
                email.trim()).stream().findFirst();
    }

    @Override
    public void atualizarSenha(Integer usuarioId, String senhaHash) {
        jdbcTemplate.update(
                "UPDATE usuarios SET senha = ? WHERE usuario_id = ?",
                senhaHash, usuarioId);
    }
}
