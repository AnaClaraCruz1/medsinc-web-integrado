package br.com.medsinc.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class CsrfController {
    @GetMapping("/csrf")
    public CsrfResponse token(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String token = (String) session.getAttribute("csrfToken");
        if (token == null) {
            token = UUID.randomUUID().toString();
            session.setAttribute("csrfToken", token);
        }
        return new CsrfResponse(token);
    }
    public record CsrfResponse(String token) {}
}
