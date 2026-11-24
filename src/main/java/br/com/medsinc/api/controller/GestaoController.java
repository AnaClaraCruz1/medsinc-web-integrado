package br.com.medsinc.api.controller;

import br.com.medsinc.application.service.BackupBancoService;
import br.com.medsinc.infrastructure.repository.GestaoJdbcRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.nio.file.Path;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class GestaoController {
    private static final Set<String> PERFIS = Set.of("administrador", "medico", "recepcionista", "farmaceutico", "tecnico");
    private final GestaoJdbcRepository repository;
    private final BackupBancoService backupService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public GestaoController(GestaoJdbcRepository repository, BackupBancoService backupService) {
        this.repository = repository;
        this.backupService = backupService;
    }

    @GetMapping("/estoque")
    public List<Map<String,Object>> estoque(HttpServletRequest req) { exigirPerfil(req, "administrador", "farmaceutico"); return repository.listarEstoque(); }

    @PostMapping("/estoque")
    public ResponseEntity<Void> criarEstoque(@RequestBody EstoqueRequest body, HttpServletRequest req) {
        int usuario = exigirPerfil(req, "administrador", "farmaceutico");
        validarTexto(body.nome(), "Nome do item", 150);
        validarTexto(body.unidade(), "Unidade", 50);
        validarQuantidades(body.quantidade(), body.minimo());
        repository.inserirEstoque(body.nome().trim(), body.quantidade(), body.unidade().trim(), body.minimo(), usuario);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/estoque/{id}")
    public void atualizarEstoque(@PathVariable int id, @RequestBody EstoqueRequest body, HttpServletRequest req) {
        int usuario = exigirPerfil(req, "administrador", "farmaceutico"); if (id <= 0); if (id <= 0) throw erro(HttpStatus.BAD_REQUEST, "ID inválido.");
        validarTexto(body.nome(), "Nome do item", 150); validarTexto(body.unidade(), "Unidade", 50);
        validarQuantidades(body.quantidade(), body.minimo());
        try { repository.atualizarEstoque(id, body.nome().trim(), body.quantidade(), body.unidade().trim(), body.minimo(), usuario); }
        catch (IllegalArgumentException ex) { throw erro(HttpStatus.NOT_FOUND, ex.getMessage()); }
    }

    @DeleteMapping("/estoque/{id}")
    public void excluirEstoque(@PathVariable int id, HttpServletRequest req) {
        exigirPerfil(req, "administrador", "farmaceutico"); try { repository.excluirEstoque(id); } catch (IllegalArgumentException ex) { throw erro(HttpStatus.NOT_FOUND, ex.getMessage()); }
    }

    @GetMapping("/historicos")
    public List<Map<String,Object>> historicos(@RequestParam(required=false) String busca, HttpServletRequest req) {
        exigirPerfil(req, "administrador", "medico"); return repository.listarHistoricos(busca);
    }

    @PostMapping("/historicos")
    public ResponseEntity<Void> criarHistorico(@RequestBody HistoricoRequest body, HttpServletRequest req) {
        exigirPerfil(req, "administrador", "medico");
        if (body.pacienteId() == null || body.pacienteId() <= 0 || body.medicoId() == null || body.medicoId() <= 0)
            throw erro(HttpStatus.BAD_REQUEST, "Selecione paciente e profissional.");
        validarTexto(body.descricao(), "Descrição", 10000);
        if (body.data() == null || body.data().isAfter(java.time.LocalDate.now()))
            throw erro(HttpStatus.BAD_REQUEST, "Informe uma data válida, não futura.");
        try { repository.inserirHistorico(body.pacienteId(), body.medicoId(), body.descricao().trim(), Date.valueOf(body.data())); }
        catch (org.springframework.dao.DataIntegrityViolationException ex) { throw erro(HttpStatus.BAD_REQUEST, "Paciente ou profissional não encontrado."); }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/usuarios")
    public List<Map<String,Object>> usuarios(HttpServletRequest req) { exigirAdmin(req); return repository.listarUsuarios(); }

    @PostMapping("/usuarios")
    public ResponseEntity<Void> criarUsuario(@RequestBody UsuarioRequest body, HttpServletRequest req) {
        exigirAdmin(req);
        validarTexto(body.nome(), "Nome", 120); validarTexto(body.email(), "E-mail", 150);
        validarTexto(body.senha(), "Senha", 100);
        if (!body.email().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) throw erro(HttpStatus.BAD_REQUEST, "E-mail inválido.");
        if (body.senha().length() < 10) throw erro(HttpStatus.BAD_REQUEST, "A senha deve ter pelo menos 10 caracteres.");
        if (body.perfil() == null || !PERFIS.contains(body.perfil())) throw erro(HttpStatus.BAD_REQUEST, "Perfil inválido.");
        try { repository.inserirUsuario(body.nome().trim(), body.email().trim(), encoder.encode(body.senha()), body.perfil()); }
        catch (DuplicateKeyException ex) { throw erro(HttpStatus.CONFLICT, "Já existe usuário com este e-mail."); }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/relatorios/resumo")
    public Map<String,Object> resumo(HttpServletRequest req) { exigirPerfil(req, "administrador", "tecnico"); return repository.resumoRelatorios(); }

    @GetMapping("/relatorios")
    public List<Map<String,Object>> relatorios(HttpServletRequest req) { exigirPerfil(req, "administrador", "tecnico"); return repository.listarRelatorios(); }

    @PostMapping("/relatorios")
    public ResponseEntity<Void> gerarRelatorio(@RequestBody RelatorioRequest body, HttpServletRequest req) {
        int usuario = exigirPerfil(req, "administrador", "tecnico");
        if (body.tipo() == null || !Set.of("desempenho", "atividades").contains(body.tipo())) throw erro(HttpStatus.BAD_REQUEST, "Tipo de relatório inválido.");
        String descricao = body.descricao() == null || body.descricao().isBlank()
                ? "Relatório " + body.tipo() + " gerado em " + java.time.LocalDate.now()
                : body.descricao().trim();
        validarTexto(descricao, "Descrição", 10000);
        repository.inserirRelatorio(body.tipo(), descricao, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/backups")
    public List<Map<String,Object>> backups(HttpServletRequest req) { exigirAdmin(req); return repository.listarBackups(); }

    @PostMapping("/backups")
    public ResponseEntity<Map<String,String>> criarBackup(HttpServletRequest req) {
        int usuario = exigirAdmin(req);
        try {
            Path arquivo = backupService.gerarBackup();
            repository.inserirBackup(arquivo.toString(), usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("arquivo", arquivo.getFileName().toString(), "mensagem", "Backup gerado no servidor."));
        } catch (RuntimeException ex) {
            throw erro(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possível gerar o backup. Verifique as permissões do diretório configurado.");
        }
    }

    private int exigir(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s == null || !(s.getAttribute("usuarioId") instanceof Integer id)) throw erro(HttpStatus.UNAUTHORIZED, "Autenticação necessária.");
        return id;
    }
    private int exigirPerfil(HttpServletRequest req, String... perfis) {
        int id = exigir(req);
        String perfil = (String) req.getSession(false).getAttribute("usuarioPerfil");
        for (String permitido : perfis) if (permitido.equals(perfil)) return id;
        throw erro(HttpStatus.FORBIDDEN, "Seu perfil não tem permissão para esta operação.");
    }
    private int exigirAdmin(HttpServletRequest req) {
        int id = exigir(req); HttpSession s = req.getSession(false);
        if (!"administrador".equals(s.getAttribute("usuarioPerfil"))) throw erro(HttpStatus.FORBIDDEN, "Acesso restrito ao perfil administrador.");
        return id;
    }
    private void validarTexto(String v, String campo, int max) {
        if (v == null || v.isBlank() || v.trim().length() > max) throw erro(HttpStatus.BAD_REQUEST, campo + " obrigatório (máximo " + max + " caracteres).");
    }
    private void validarQuantidades(Integer q, Integer m) {
        if (q == null || m == null || q < 0 || m < 0) throw erro(HttpStatus.BAD_REQUEST, "As quantidades devem ser números inteiros não negativos.");
    }
    private ResponseStatusException erro(HttpStatus status, String mensagem) { return new ResponseStatusException(status, mensagem); }

    public record EstoqueRequest(String nome, Integer quantidade, String unidade, Integer minimo) {}
    public record HistoricoRequest(Integer pacienteId, Integer medicoId, java.time.LocalDate data, String descricao) {}
    public record UsuarioRequest(String nome, String email, String senha, String perfil) {}
    public record RelatorioRequest(String tipo, String descricao) {}
}
