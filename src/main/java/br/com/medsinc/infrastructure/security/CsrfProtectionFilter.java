package br.com.medsinc.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class CsrfProtectionFilter extends OncePerRequestFilter {
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return "GET".equalsIgnoreCase(request.getMethod())
                || "HEAD".equalsIgnoreCase(request.getMethod())
                || "OPTIONS".equalsIgnoreCase(request.getMethod())
                || "/api/auth/login".equals(path)
                || "/api/auth/csrf".equals(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String expected = session == null ? null : (String) session.getAttribute("csrfToken");
        String supplied = request.getHeader("X-CSRF-TOKEN");
        boolean valid = expected != null && supplied != null
                && MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),
                        supplied.getBytes(StandardCharsets.UTF_8));
        if (!valid) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token de segurança ausente ou inválido.");
            return;
        }
        chain.doFilter(request, response);
    }
}
