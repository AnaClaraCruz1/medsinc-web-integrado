package br.com.medsinc.api.controller;

import br.com.medsinc.api.dto.LoginRequest;
import br.com.medsinc.api.dto.UsuarioAutenticadoResponse;
import br.com.medsinc.application.service.AutenticacaoWebService;
import br.com.medsinc.application.service.UsuarioAutenticacao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AutenticacaoController {

    private final AutenticacaoWebService service;

    public AutenticacaoController(AutenticacaoWebService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public UsuarioAutenticadoResponse login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        UsuarioAutenticacao usuario = service.autenticar(request.email(), request.senha())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos."));

        HttpSession sessaoAnterior = httpRequest.getSession(false);
        if (sessaoAnterior != null) {
            sessaoAnterior.invalidate();
        }

        HttpSession sessao = httpRequest.getSession(true);
        sessao.setAttribute("usuarioId", usuario.id());
        sessao.setAttribute("usuarioNome", usuario.nome());
        sessao.setAttribute("usuarioEmail", usuario.email());
        sessao.setAttribute("usuarioPerfil", usuario.perfil());

        return resposta(usuario);
    }

    @GetMapping("/me")
    public UsuarioAutenticadoResponse usuarioAtual(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        if (sessao == null || sessao.getAttribute("usuarioId") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Autenticação necessária.");
        }

        return new UsuarioAutenticadoResponse(
                (Integer) sessao.getAttribute("usuarioId"),
                (String) sessao.getAttribute("usuarioNome"),
                (String) sessao.getAttribute("usuarioEmail"),
                (String) sessao.getAttribute("usuarioPerfil"));
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        if (sessao != null) {
            sessao.invalidate();
        }
    }

    private UsuarioAutenticadoResponse resposta(UsuarioAutenticacao usuario) {
        return new UsuarioAutenticadoResponse(
                usuario.id(), usuario.nome(), usuario.email(), usuario.perfil());
    }
}
