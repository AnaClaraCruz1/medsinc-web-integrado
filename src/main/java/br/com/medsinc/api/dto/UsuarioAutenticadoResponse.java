package br.com.medsinc.api.dto;

public record UsuarioAutenticadoResponse(
        Integer id,
        String nome,
        String email,
        String perfil
) {}
