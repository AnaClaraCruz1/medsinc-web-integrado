package br.com.medsinc.api.controller;

import br.com.medsinc.api.dto.ConsultaCadastroRequest;
import br.com.medsinc.api.dto.ConsultaResponse;
import br.com.medsinc.application.service.ConsultaService;
import br.com.medsinc.infrastructure.repository.ConsultaJdbcRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

    private final ConsultaService service;

    public ConsultaController(ConsultaService service) {
        this.service = service;
    }

    @GetMapping
    public List<ConsultaResponse> listar(
            @RequestParam(name = "busca", required = false) String busca,
            HttpServletRequest request) {
        exigirAutenticacao(request);
        return service.listar(busca);
    }

    @GetMapping("/profissionais")
    public List<ConsultaJdbcRepository.Profissional> listarMedicos(HttpServletRequest request) {
        exigirAutenticacao(request);
        return service.listarMedicos();
    }

    @PostMapping
    public ResponseEntity<Void> cadastrar(
            @Valid @RequestBody ConsultaCadastroRequest dados,
            HttpServletRequest request) {
        exigirAutenticacao(request);
        try {
            service.cadastrar(dados);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Paciente ou profissional não encontrado. Confira os dados selecionados.");
        }
    }

    private void exigirAutenticacao(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        if (sessao == null || !(sessao.getAttribute("usuarioId") instanceof Integer)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Autenticação necessária.");
        }
    }
}
