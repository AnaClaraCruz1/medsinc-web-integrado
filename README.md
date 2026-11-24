# MEDSiNC — Aplicação Web Integrada

Projeto Integrador desenvolvido para a Etapa 9, reunindo back-end Java, persistência MySQL, interface web e testes das etapas anteriores.

## Objetivo

Evoluir a interface da Etapa 8 para uma aplicação integrada, com API REST em Spring Boot e persistência JDBC/MySQL, reaproveitando componentes de domínio da Etapa 6 e testes JUnit da Etapa 7.

## Tecnologias

- Java 17 como versão-alvo do projeto (validado localmente com Java 24.0.2)
- Spring Boot 3.5.7, Spring Web, Spring JDBC e Bean Validation
- MySQL e Maven
- JUnit 5, Mockito e Spring Boot Test
- HTML, CSS e JavaScript

## Configuração

A conexão utiliza variáveis de ambiente:

- `MEDSINC_DB_URL`
- `MEDSINC_DB_USER`
- `MEDSINC_DB_PASSWORD`

Não incluir credenciais, dumps com dados pessoais ou arquivos locais de configuração no Git. A aplicação não executa migrações automaticamente (`spring.sql.init.mode=never`).

## Execução local

Na pasta que contém `pom.xml`, com JDK e Maven configurados:

```powershell
mvn test
mvn spring-boot:run
```

Se o Maven não estiver no PATH no Windows, pode-se chamar o executável Maven instalado com o NetBeans. Configure `JAVA_HOME` apenas para a sessão do terminal se necessário.

A aplicação web é servida em `http://localhost:8080`.

## Estrutura

- `src/main/java/br/com/medsinc/api`: controllers e DTOs.
- `src/main/java/br/com/medsinc/application`: serviços e casos de uso.
- `src/main/java/br/com/medsinc/infrastructure`: persistência JDBC.
- `src/main/resources/static`: páginas e recursos da interface.
- `src/main/resources/db/migration`: migrações SQL manuais e versionadas.
- `src/test`: testes automatizados.
- `docs`: arquitetura, contratos, plano de integração, testes e evidências.

## Segurança — autenticação

A API disponibiliza:

- `POST /api/auth/login`: recebe JSON com `email` e `senha`; autentica contra a tabela `usuarios) e cria sessão HTTP.
- `GET /api/auth/me`: retorna os dados não sensíveis do usuário da sessão.
- `POST /api/auth/logout`: invalida a sessão.

Senhas legadas em texto simples são verificadas para compatibilidade e, após login válido, atualizadas para hash BCrypt. Senhas já armazenadas em BCrypt são verificadas sem alteração. A resposta nunca inclui senha ou hash.

A interface de login já envia as credenciais à API. A proteção de todas as páginas e operações por sessão/perfil ainda precisa ser concluída; portanto, esta autenticação inicial não deve ser tratada como implantação de produção.

## Pacientes — status da integração

- Leitura e pesquisa via `GET /api/pacientes) e `GET /api/pacientes/{id}`.
- Migração local adicionou CPF, data de nascimento, telefone, e-mail e convênio sem remover colunas antigas; índice único em CPF.
- Cadastro, edição, exclusão e integração completa das telas com a API ainda estão pendentes.

## Materiais incorporados

- Interface da Etapa 8 (cinco páginas HTML, CSS e JavaScript).
- Modelos de domínio da Etapa 6.
- Testes JUnit da Etapa 7: `PacienteTest`, `ConsultaTest` e `ItemEstoqueTest` (17 métodos).
- Plano de testes e script SQL original.

Fontes: [Etapa 6 e Atividade 7](https://github.com/AnaClaraCruz1/medsinc-refatorado/tree/atividade-7) e [Etapa 8](https://github.com/AnaClaraCruz1/medsinc-web).

## Validações locais registradas em 24/11/2026

- `mvn test): 21 testes executados, zero falhas/erros/ignorados, BUILD SUCCESS, antes da inclusão dos testes de autenticação.
- `mvn spring-boot:run): servidor iniciado na porta 8080.
- Páginas inicial e consulta de pacientes abriram via servidor.
- `GET /api/pacientes): retornou 16 registros.
- Pesquisa por termo inexistente: retornou zero registros.

Os novos testes de autenticação e as alterações mais recentes ainda precisam ser executados localmente. O funcionamento de login também precisa ser validado com uma conta de teste autorizada.

## Entrega

A conclusão depende de testes integrados, evidências reais, registro de defeitos encontrados e preparação do ZIP final. Não usar dados reais de pacientes nas capturas ou arquivos de entrega.
