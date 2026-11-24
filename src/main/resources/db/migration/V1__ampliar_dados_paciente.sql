-- Migracao incremental da Etapa 9.
-- Executar uma unica vez em banco criado pela estrutura da Etapa 6.
-- Preserva os campos e registros legados; os novos campos ficam NULL para registros antigos.

ALTER TABLE pacientes
    ADD COLUMN cpf VARCHAR(14) NULL AFTER nome,
    ADD COLUMN data_nascimento DATE NULL AFTER cpf,
    ADD COLUMN telefone VARCHAR(20) NULL AFTER contato,
    ADD COLUMN email VARCHAR(150) NULL AFTER telefone,
    ADD COLUMN convenio VARCHAR(80) NULL AFTER email;

-- MySQL permite varios NULL em indice UNIQUE; registros legados permanecem validos.
-- A aplicacao normaliza CPF para 000.000.000-00 antes de persistir.
CREATE UNIQUE INDEX uq_pacientes_cpf ON pacientes (cpf);
