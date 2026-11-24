package br.com.medsinc.domain.model;

public record Paciente(Integer id, String nome, int idade, String endereco, String contato, int usuarioCadastroId) {
    public Paciente {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("O nome do paciente é obrigatório.");
        if (idade < 0) throw new IllegalArgumentException("A idade não pode ser negativa.");
        if (usuarioCadastroId <= 0) throw new IllegalArgumentException("O usuário responsável pelo cadastro é obrigatório.");
    }
    public Paciente semId() { return new Paciente(null, nome, idade, endereco, contato, usuarioCadastroId); }
}
