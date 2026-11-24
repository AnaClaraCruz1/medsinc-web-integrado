package br.com.medsinc.application.service;

import br.com.medsinc.infrastructure.repository.UsuarioAutenticacaoRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticacaoWebServiceTest {

    @Mock
    private UsuarioAutenticacaoRepository repository;

    @InjectMocks
    private AutenticacaoWebService service;

    @Test
    void deveAutenticarSenhaLegadaEAtualizarParaBcrypt() {
        UsuarioAutenticacao usuario = new UsuarioAutenticacao(
                7, "Usuário Teste", "teste@medsinc.com", "senha-legada", "administrador");
        when(repository.buscarPorEmail("teste@medsinc.com")).thenReturn(Optional.of(usuario));

        Optional<UsuarioAutenticacao> resultado = service.autenticar("teste@medsinc.com", "senha-legada");

        assertTrue(resultado.isPresent());
        verify(repository).atualizarSenha(eq(7), argThat(hash ->
                hash.startsWith("$2") && new BCryptPasswordEncoder().matches("senha-legada", hash)));
    }

    @Test
    void deveRejeitarSenhaIncorreta() {
        UsuarioAutenticacao usuario = new UsuarioAutenticacao(
                7, "Usuário Teste", "teste@medsinc.com", "senha-legada", "administrador");
        when(repository.buscarPorEmail("teste@medsinc.com")).thenReturn(Optional.of(usuario));

        assertTrue(service.autenticar("teste@medsinc.com", "incorreta").isEmpty());
        verify(repository, never()).atualizarSenha(anyInt(), anyString());
    }

    @Test
    void deveValidarSenhaQueJaEstaEmBcryptSemAtualizarNovamente() {
        String hash = new BCryptPasswordEncoder().encode("senha-segura");
        UsuarioAutenticacao usuario = new UsuarioAutenticacao(
                7, "Usuário Teste", "teste@medsinc.com", hash, "administrador");
        when(repository.buscarPorEmail("teste@medsinc.com")).thenReturn(Optional.of(usuario));

        assertTrue(service.autenticar("teste@medsinc.com", "senha-segura").isPresent());
        verify(repository, never()).atualizarSenha(anyInt(), anyString());
    }

    @Test
    void deveRejeitarCredenciaisVaziasSemConsultarRepositorio() {
        assertTrue(service.autenticar(" ", "senha").isEmpty());
        assertTrue(service.autenticar("teste@medsinc.com", " ").isEmpty());
        verifyNoInteractions(repository);
    }
}
