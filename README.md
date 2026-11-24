# MEDSiNC — Aplicação Web Integrada

Projeto Integrador da Etapa 9: aplicação web Java com Spring Boot, API REST, persistência MySQL, interface integrada e testes automatizados das etapas anteriores.

## Tecnologias
- Java 17 (compatível com execução local em Java 24)
- Spring Boot 3.5.7, Spring Web, Spring JDBC e Bean Validation
- MySQL 8, Maven, JUnit 5 e Mockito
- HTML, CSS e JavaScript

## Configuração e execução
A conexão utiliza as variáveis de ambiente `MEDSINC_DB_URL`, `MEDSINC_DB_USER` e `MEDSINC_DB_PASSWORD`. A aplicação não executa migrações automaticamente.

Na pasta que contém `pom.xml`:
```powershell
mvn clean test
mvn spring-boot:run
```
No Windows, se o Maven não estiver no PATH, use o executável Maven instalado com o NetBeans e configure `JAVA_HOME` apenas para a sessão do terminal. A aplicação é servida em http://localhost:8080.

O diretório de backups pode ser configurado por `MEDSINC_BACKUP_DIR`; o padrão é `./backups`. O processo Java precisa ter permissão de escrita nesse local.

## Organização
- `api/controller` e `api/dto`: endpoints REST e contratos.
- `application/service`: regras de negócio.
- `infrastructure/repository`: persistência JDBC.
- `infrastructure/security`: proteção CSRF.
- `resources/static`: páginas, CSS e JavaScript.
- `resources/db/migration`: migrações SQL manuais.
- `src/test`: testes automatizados.
- `docs`: arquitetura, contratos, plano e evidências.

## Autenticação e segurança
- `POST /api/auth/login`, `GET /api/auth/me` e `POST /api/auth/logout` gerenciam a sessão.
- `GET /api/auth/csrf` emite token associado à sessão. Requisições POST, PUT e DELETE exigem o cabeçalho `X-CSRF-TOKEN`.
- O cookie de sessão é HttpOnly e SameSite=Lax.
- Senhas legadas são migradas para BCrypt após login válido; novas senhas também são armazenadas com BCrypt.
- Respostas de usuários nunca incluem hashes ou senhas.
- Estoque: administrador e farmacêutico.
- Histórico médico: administrador e médico.
- Usuários e backups: somente administrador.
- Relatórios: administrador e técnico.
- Os demais endpoints exigem sessão autenticada.

Esta é uma implementação acadêmica. Antes de produção, ainda são necessários hardening, limitação de tentativas, política de retenção, revisão de permissões e testes de segurança independentes.

## Módulos integrados
| Módulo | Operações principais |
|---|---|
| Pacientes | Consulta, pesquisa e cadastro via API; CPF único e idade calculada no servidor |
| Consultas | Listagem, pesquisa, seleção de paciente/médico por ID e cadastro |
| Estoque | Listagem, cadastro, edição e exclusão; cálculo de nível mínimo |
| Histórico médico | Listagem e cadastro vinculados a paciente e médico |
| Usuários | Listagem sem credenciais e cadastro administrativo com senha BCrypt |
| Relatórios | Indicadores calculados no MySQL e registro de relatórios |
| Backups | Geração de arquivo SQL de dados no servidor e histórico no MySQL |

O backup é um arquivo SQL de dados e pressupõe que o esquema do projeto já exista no banco de destino. O arquivo contém dados pessoais e hashes de senha: mantenha-o em diretório protegido, não o publique no GitHub e não o inclua no ZIP acadêmico. O script de esquema e a migração estão em `docs/banco-de-dados` e `src/main/resources/db/migration`.

## Pacientes e banco legado
A migração incremental adicionou CPF, nascimento, telefone, e-mail e convênio sem remover as colunas legadas nem os registros existentes. Registros antigos podem permanecer sem CPF. CPF é normalizado e protegido por índice único; não há validação matemática dos dígitos verificadores, conforme decisão funcional. O ID do usuário responsável vem da sessão, nunca do navegador.

## Materiais incorporados
- Interface da Etapa 8.
- Modelos de domínio da Etapa 6.
- Testes JUnit da Etapa 7: PacienteTest, ConsultaTest e ItemEstoqueTest (17 métodos).
- Plano de testes e script SQL original.

Fontes: https://github.com/AnaClaraCruz1/medsinc-refatorado/tree/atividade-7 e https://github.com/AnaClaraCruz1/medsinc-web.

## Validação
Antes das últimas implementações, a execução local registrada em 24/11/2026 teve 21 testes aprovados, servidor Spring Boot iniciado na porta 8080 e fluxo de pacientes validado manualmente. Os novos módulos ainda aguardam a validação consolidada no ambiente local.

O repositório contém workflow GitHub Actions para validar sintaxe JavaScript e executar `mvn clean test` em Java 17. A conclusão da etapa exige CI verde, execução local integrada, evidências reais, registro e correção de defeitos encontrados e preparação do ZIP final.

Use somente dados fictícios nas capturas e nos arquivos de entrega.
