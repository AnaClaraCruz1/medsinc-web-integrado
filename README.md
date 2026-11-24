# MEDSiNC — Aplicação Web Integrada

Projeto Integrador desenvolvido para a Etapa 9, reunindo o back-end Java, a persistência MySQL, a interface web e os testes das etapas anteriores.

## Objetivo

Evoluir a interface demonstrativa da Etapa 8 para uma aplicação web integrada, com API REST em Spring Boot e persistência JDBC/MySQL, reaproveitando e adaptando os componentes de domínio da Etapa 6 e os testes JUnit da Etapa 7.

## Tecnologias previstas

- Java (versão definida após conferir a compatibilidade do ambiente)
- Spring Boot e Spring Web (API REST)
- JDBC e MySQL
- Maven
- JUnit 5 e Spring Boot Test
- HTML, CSS e JavaScript

## Organização

```text
medsinc-web-integrado/
├── docs/
│   ├── arquitetura/
│   ├── evidencias/
│   │   ├── testes/
│   │   ├── bugs/
│   │   └── versionamento/
│   ├── plano-de-testes/
│   └── banco-de-dados/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │       ├── static/
│   │       └── db/
│   └── test/
└── pom.xml
```

A estrutura está sendo preenchida por módulos. A primeira vertical iniciada é a consulta de pacientes.

## Configuração do banco

A aplicação utilizará as variáveis de ambiente já definidas no ambiente de execução:

- `MEDSINC_DB_URL`
- `MEDSINC_DB_USER`
- `MEDSINC_DB_PASSWORD`

Não incluir credenciais no código-fonte ou no repositório. O script de banco e eventuais migrações serão documentados em `docs/banco-de-dados/`.

## Etapas de integração

1. Inventariar e selecionar os componentes validados das Etapas 6, 7 e 8.
2. Definir o contrato da API e adaptar os modelos/DTOs para os campos da interface.
3. Implementar persistência e serviços com tratamento consistente de erros.
4. Integrar as páginas estáticas à API.
5. Incorporar os testes existentes e acrescentar cobertura para os fluxos novos.
6. Executar o plano de testes, registrar defeitos e correções.
7. Preparar evidências de testes e versionamento para a entrega.

## Segurança e dados

- Senhas devem ser armazenadas com hash seguro, nunca em texto puro.
- Segredos e dados pessoais reais não devem ser enviados ao Git.
- Dados fictícios devem ser usados nas evidências.
- A autenticação e as permissões serão implementadas e testadas antes de serem consideradas funcionais.

## Materiais já incorporados

- Interface da Etapa 8: cinco páginas HTML, folhas CSS e scripts JavaScript em `src/main/resources/static/`.
- Modelos de domínio da Etapa 6: paciente, consulta, item de estoque, histórico médico e usuário.
- Testes JUnit da Etapa 7: classes `PacienteTest`, `ConsultaTest` e `ItemEstoqueTest` (17 métodos de teste).
- Plano de testes da Atividade 7 e script SQL original, preservados em `docs/`.
- Projeto Maven configurado com Spring Boot 3.5.7 e Java 17.

Fontes: [Etapa 6 e Atividade 7](https://github.com/AnaClaraCruz1/medsinc-refatorado/tree/atividade-7) e [Etapa 8](https://github.com/AnaClaraCruz1/medsinc-web).

## Status

**Integração de leitura de pacientes iniciada.** Foram criados DTO de resposta, repositório JDBC com JdbcTemplate, serviço, endpoints GET e teste unitário do serviço. Foi criada uma migração SQL incremental para os campos novos do formulário, preservando as colunas legadas. A migração ainda precisa ser executada no banco local. Cadastro/edição/exclusão, autenticação/autorização e integração do JavaScript com a API ainda não estão concluídos. O projeto ainda precisa ser compilado e executado no ambiente local; o novo teste ainda não foi executado nesta etapa.
