package br.com.medsinc.application.service;

public record UsuarioAutenticacao(
        Integer id,
        String nome,
        String email,
        String senhaArmazenada,
        String perfil
) {}
