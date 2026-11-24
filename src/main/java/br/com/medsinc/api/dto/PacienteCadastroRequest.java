package br.com.medsinc.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record PacienteCadastroRequest(
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @Pattern(regexp = "[0-9.\\- ]{11,14}") String cpf,
        @NotNull @PastOrPresent LocalDate nascimento,
        @NotBlank @Pattern(regexp = "[0-9() +\\-]{10,20}") String telefone,
        @Email @Size(max = 150) String email,
        @NotBlank @Size(max = 255) String endereco,
        @NotBlank @Size(max = 80) String convenio
) {}
