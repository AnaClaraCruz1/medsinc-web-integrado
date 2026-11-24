package br.com.medsinc.application.service;

import br.com.medsinc.infrastructure.repository.UsuarioAutenticacaoRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutenticacaoWebService {

    private final UsuarioAutenticacaoRepository repository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AutenticacaoWebService(UsuarioAutenticacaoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Optional<UsuarioAutenticacao> autenticar(String email, String senha) {
        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            return Optional.empty();
        }

        Optional<UsuarioAutenticacao> encontrado = repository.buscarPorEmail(email);
        if (encontrado.isEmpty()) {
            return Optional.empty();
        }

        UsuarioAutenticacao usuario = encontrado.get();
        String armazenada = usuario.senhaArmazenada();
        if (armazenada == null || armazenada.isBlank()) {
            return Optional.empty();
        }

        boolean senhaValida;
        if (ehHashBcrypt(armazenada)) {
            senhaValida = passwordEncoder.matches(senha, armazenada);
        } else {
            senhaValida = MessageDigest.isEqual(
                    senha.getBytes(StandardCharsets.UTF_8),
                    armazenada.getBytes(StandardCharsets.UTF_8));
            if (senhaValida) {
                repository.atualizarSenha(usuario.id(), passwordEncoder.encode(senha));
            }
        }

        return senhaValida ? Optional.of(usuario) : Optional.empty();
    }

    private boolean ehHashBcrypt(String valor) {
        return valor.startsWith("$2a$") || valor.startsWith("$2b$") || valor.startsWith("$2y$");
    }
}
