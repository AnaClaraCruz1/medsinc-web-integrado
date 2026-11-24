package br.com.medsinc.infrastructure.repository;

import br.com.medsinc.application.service.UsuarioAutenticacao;
import java.util.Optional;

public interface UsuarioAutenticacaoRepository {
    Optional<UsuarioAutenticacao> buscarPorEmail(String email);
    void atualizarSenha(Integer usuarioId, String senhaHash);
}
