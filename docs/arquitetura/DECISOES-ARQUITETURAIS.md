# Arquitetura inicial — MEDSiNC Web Integrado

## Decisão arquitetural

Aplicação Spring Boot com API REST e interface estática servida pelo próprio Spring Boot. O navegador consome endpoints JSON por `fetch()`, mantendo a interface da Etapa 8 no mesmo servidor.

## Camadas

| Camada | Responsabilidade |
|---|---|
| `api/controller` | Endpoints REST, códigos HTTP e validação de entrada |
| `api/dto` | Contratos de requisição e resposta |
| `application/service` | Regras de negócio e casos de uso |
| `infrastructure/repository` | Persistência JDBC |
| `domain/model` | Entidades e enums de domínio |
| `resources/static` | HTML, CSS e JavaScript |
| `resources/db` | Migrações SQL manuais |

## Decisões e regras

1. Reaproveitar componentes validados da Etapa 6 e preservar os 17 testes JUnit da Etapa 7.
2. Manter a interface da Etapa 8 e substituir progressivamente o armazenamento demonstrativo por chamadas à API.
3. Usar DTOs; não expor entidades JDBC diretamente.
4. Consultas e histórico devem trafegar com IDs relacionais, mesmo quando a interface exibir nomes.
5. Identidade autenticada deve determinar os campos de auditoria; nunca fixar IDs de usuário.
6. Não expor credenciais, hashes ou dados pessoais em respostas, logs ou evidências.

## Compatibilização de pacientes

O formulário contém CPF, nascimento, telefone, e-mail e convênio; o esquema legado tinha apenas nome, idade, endereço e contato. A migração incremental adicionou as novas colunas, mantendo as antigas e os registros. A idade deve ser calculada a partir da data de nascimento para novos registros. CPF é normalizado e deve ser único, mas não passa por validação matemática dos dígitos, conforme decisão funcional.

## Autenticação e sessão implementadas inicialmente

- `POST /api/auth/login`: autentica pela coluna `email` (aceita também identificadores legados) e senha da tabela `usuarios`, cria sessão HTTP e devolve somente ID, nome, e-mail e perfil.
- `GET /api/auth/me`: consulta a identidade mantida na sessão.
- `POST /api/auth/logout`: invalida a sessão.
- Senhas legadas em texto simples são comparadas em tempo constante e migradas para BCrypt após autenticação válida. Hashes BCrypt existentes são verificados com o encoder.
- Cookie de sessão configurado como HttpOnly e SameSite=Lax.
- A interface inicial chama o endpoint real; painel e módulos verificam a sessão.
- Endpoints de leitura e cadastro de pacientes exigem sessão.

**Limite atual:** autorização detalhada por perfil, proteção CSRF dedicada, limitação de tentativas e testes de ponta a ponta ainda precisam ser implementados antes de considerar a segurança concluída.

## Cadastro de pacientes implementado inicialmente

- `POST /api/pacientes` valida os campos no servidor, normaliza CPF para 11 dígitos, calcula idade e grava o usuário responsável a partir da sessão.
- O índice UNIQUE do banco é a proteção final contra CPF duplicado; conflito retorna HTTP 409.
- A tela de cadastro envia o formulário à API e a tela de consulta carrega os registros pela API.
- A edição e a exclusão ainda não foram implementadas.

## Segurança e dados

- Credenciais somente por variáveis de ambiente.
- Validar entradas no servidor.
- Não versionar dumps ou dados pessoais.
- Usar dados fictícios em testes e capturas.
- Migrações SQL manuais; não são executadas automaticamente pela aplicação.

## Critério de conclusão

Uma funcionalidade só será considerada concluída após código, execução de testes e evidência correspondente. Este documento registra decisões e o estado parcial da implementação, não certifica funcionalidades ainda não testadas.
