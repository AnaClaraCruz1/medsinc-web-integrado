package br.com.medsinc.domain.model;

public record Usuario(Integer id, String nome, String email, String senha, String perfil) {
    public Usuario {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("O nome é obrigatório.");
        if (email == null || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) throw new IllegalArgumentException("Informe um e-mail válido.");
        if (senha == null || senha.isBlank()) throw new IllegalArgumentException("A senha é obrigatória.");
        if (perfil == null || perfil.isBlank()) throw new IllegalArgumentException("O perfil é obrigatório.");
    }
}
