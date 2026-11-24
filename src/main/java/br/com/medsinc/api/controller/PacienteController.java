package br.com.medsinc.api.controller;

import br.com.medsinc.api.dto.PacienteCadastroRequest;
import br.com.medsinc.api.dto.PacienteResponse;
import br.com.medsinc.application.service.PacienteCadastroService;
import br.com.medsinc.application.service.PacienteConsultaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteConsultaService consultaService;
    private final PacienteCadastroService cadastroService;

    public PacienteController(
            PacienteConsultaService consultaService,
            PacienteCadastroService cadastroService) {
        this.consultaService = consultaService;
        this.cadastroService = cadastroService;
    }

    @GetMapping
    public List<PacienteResponse> listar(
            @RequestParam(name = "busca", required = false) String busca,
            HttpServletRequest request) {
        exigirAutenticacao(request);
        return consultaService.listar(busca);
    }

    @GetMapping("/{id}")
    public PacienteResponse buscarPorId(@PathVariable int id, HttpServletRequest request) {
        exigirAutenticacao(request);
        try {
            return consultaService.buscarPorId(id);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identificador inválido.");
        } catch (PacienteConsultaService.PacienteNaoEncontradoException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente não encontrado.");
        }
    }

    @PostMapping
    public ResponseEntity<PacienteResponse> cadastrar(
            @Valid @RequestBody PacienteCadastroRequest dados,
            HttpServletRequest request) {
        Integer usuarioId = exigirAutenticacao(request);
        try {
            PacienteResponse criado = cadastroService.cadastrar(dados, usuarioId);
            return ResponseEntity.status(HttpStatus.CREATED).body(criado);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (DuplicateKeyException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um paciente cadastrado com este CPF.");
        }
    }

    private Integer exigirAutenticacao(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        if (sessao == null || !(sessao.getAttribute("usuarioId") instanceof Integer usuarioId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Autenticação necessária.");
        }
        return usuarioId;
    }
}
