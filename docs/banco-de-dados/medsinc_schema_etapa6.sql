-- Script de estrutura do banco Medsinc para fins acadêmicos.
-- INSERTs com dados demonstrativos foram removidos desta cópia de entrega.

CREATE DATABASE medsinc
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;

USE medsinc;

CREATE TABLE usuarios (
    usuario_id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(100) NOT NULL,
    perfil ENUM(
        'administrador',
        'medico',
        'recepcionista',
        'farmaceutico',
        'tecnico'
    ) NOT NULL,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE pacientes (
    paciente_id INT AUTO_INCREMENT PRIMARY KEY,
    
    nome VARCHAR(120) NOT NULL,
    idade INT NOT NULL,
    endereco VARCHAR(255),
    contato VARCHAR(150),

    usuario_cadastro_id INT NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_paciente_usuario
    FOREIGN KEY (usuario_cadastro_id)
    REFERENCES usuarios(usuario_id)
);

CREATE TABLE consultas (
    consulta_id INT AUTO_INCREMENT PRIMARY KEY,

    paciente_id INT NOT NULL,
    medico_id INT NOT NULL,

    data_consulta DATE NOT NULL,
    hora_consulta TIME NOT NULL,

    status ENUM(
        'agendada',
        'realizada',
        'cancelada'
    ) DEFAULT 'agendada',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_consulta_paciente
    FOREIGN KEY (paciente_id)
    REFERENCES pacientes(paciente_id),

    CONSTRAINT fk_consulta_medico
    FOREIGN KEY (medico_id)
    REFERENCES usuarios(usuario_id)
);

CREATE TABLE historicos_medicos (
    historico_id INT AUTO_INCREMENT PRIMARY KEY,

    paciente_id INT NOT NULL,
    medico_id INT NOT NULL,

    descricao TEXT NOT NULL,
    data_registro DATE NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_historico_paciente
    FOREIGN KEY (paciente_id)
    REFERENCES pacientes(paciente_id),

    CONSTRAINT fk_historico_medico
    FOREIGN KEY (medico_id)
    REFERENCES usuarios(usuario_id)
);

CREATE TABLE estoque (
    item_id INT AUTO_INCREMENT PRIMARY KEY,

    nome_item VARCHAR(150) NOT NULL,
    quantidade INT NOT NULL,
    unidade_medida VARCHAR(50),

    quantidade_minima INT DEFAULT 0,

    atualizado_por INT NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_estoque_usuario
    FOREIGN KEY (atualizado_por)
    REFERENCES usuarios(usuario_id)
);

CREATE TABLE relatorios (
    relatorio_id INT AUTO_INCREMENT PRIMARY KEY,

    tipo ENUM(
        'desempenho',
        'atividades'
    ) NOT NULL,

    descricao TEXT,

    gerado_por INT NOT NULL,
    data_geracao DATE NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_relatorio_usuario
    FOREIGN KEY (gerado_por)
    REFERENCES usuarios(usuario_id)
);

CREATE TABLE backups (
    backup_id INT AUTO_INCREMENT PRIMARY KEY,

    caminho_arquivo VARCHAR(255) NOT NULL,
    data_backup DATETIME NOT NULL,

    realizado_por INT NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_backup_usuario
    FOREIGN KEY (realizado_por)
    REFERENCES usuarios(usuario_id)
);


CREATE INDEX idx_paciente_nome
ON pacientes(nome);

CREATE INDEX idx_consulta_data
ON consultas(data_consulta);

CREATE INDEX idx_estoque_nome
ON estoque(nome_item);

ALTER TABLE consultas
DROP FOREIGN KEY fk_consulta_paciente;

ALTER TABLE consultas
ADD CONSTRAINT fk_consulta_paciente
FOREIGN KEY (paciente_id)
REFERENCES pacientes(paciente_id)
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE historicos_medicos
DROP FOREIGN KEY fk_historico_paciente;

ALTER TABLE historicos_medicos
ADD CONSTRAINT fk_historico_paciente
FOREIGN KEY (paciente_id)
REFERENCES pacientes(paciente_id)
ON DELETE CASCADE
ON UPDATE CASCADE;
