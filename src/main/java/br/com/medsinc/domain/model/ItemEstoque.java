package br.com.medsinc.domain.model;

public record ItemEstoque(Integer id, String nome, int quantidade, String unidadeMedida, int quantidadeMinima, int atualizadoPor) {
    public ItemEstoque {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("O nome do item é obrigatório.");
        if (quantidade < 0 || quantidadeMinima < 0) throw new IllegalArgumentException("As quantidades não podem ser negativas.");
        if (atualizadoPor <= 0) throw new IllegalArgumentException("O usuário responsável deve ser informado.");
    }
    public boolean noLimiteOuAbaixoDoMinimo() { return quantidade <= quantidadeMinima; }
}
