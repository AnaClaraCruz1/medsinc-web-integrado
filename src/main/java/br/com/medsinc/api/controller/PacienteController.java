package br.com.medsinc.api.controller;

import br.com.medsinc.api.dto.PacienteResponse;
import br.com.medsinc.application.service.PacienteConsultaService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteConsultaService service;

    public PacienteController(PacienteConsultaService service) {
        this.service = service;
    }

    @GetMapping
    public List<PacienteResponse> listar(
            @RequestParam(name = "busca", required = false) String busca) {
        return service.listar(busca);
    }

    @GetMapping("/{id}")
    public PacienteResponse buscarPorId(@PathVariable int id) {
        try {
            return service.buscarPorId(id);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identificador inválido.");
        } catch (PacienteConsultaService.PacienteNaoEncontradoException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente não encontrado.");
        }
    }
}
