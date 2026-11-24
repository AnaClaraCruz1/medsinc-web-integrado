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

A estrutura será preenchida gradualmente conforme a integração e validação dos módulos.

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

## Status

**Em organização e integração.** A presença de arquivos neste repositório não significa, por si só, que funcionalidades já estejam implementadas ou testadas.
