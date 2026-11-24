# MEDSiNC — Aplicação Web Integrada

Projeto Integrador da Etapa 9, reunindo back-end Java, persistência MySQL, interface web e testes das etapas anteriores.

## Tecnologias

- Java 17 como versão-alvo (execução local anterior em Java 24.0.2)
- Spring Boot 3.5.7, Spring Web, Spring JDBC e Bean Validation
- MySQL, Maven, JUnit 5, Mockito
- HTML, CSS e JavaScript

## Configuração e execução

A conexão utiliza as variáveis de ambiente `MEDSINC_DB_URL`, `MEDSINC_DB_USER` e `MEDSINC_DB_PASSWORD`. Não incluir credenciais, dumps com dados pessoais ou arquivos locais de configuração no Git. A aplicação não executa migrações automaticamente.

Na pasta que contém `pom.xml`:

```powershell
mvn test
mvn spring-boot:run
```

No Windows, se o Maven não estiver no PATH, pode-se chamar o executável Maven instalado com o NetBeans e configurar `JAVA_HOME` apenas para a sessão do terminal.

A aplicação é servida em `http://localhost:8080`.

## Organização

- `api/controller` e `api/dto`: endpoints REST e contratos.
- `application/service`: regras de negócio.
- `infrastructure/repository`: persistência JDBC.
- `resources/static`: páginas e recursos web.
- `resources/db/migration`: migrações SQL manuais.
- `src/test`: testes automatizados.
- `docs`: arquitetura, contratos, plano e evidências.

## Autenticação e sessão

- `POST /api/auth/login`: recebe JSON com `email` (ou identificador legado) e `senha), valida contra `usuarios) e cria sessão HTTP.
- `GET /api/auth/me`: retorna somente ID, nome, e-mail e perfil da sessão.
- `POST /api/auth/logout`: invalida a sessão.
- Senhas legadas em texto simples são verificadas por compatibilidade e atualizadas para BCrypt após autenticação válida. Hashes BCrypt existentes são verificados sem alteração.
- O cookie de sessão é HttpOnly e SameSite=Lax.
- As páginas de painel/módulos e os endpoints de pacientes exigem sessão. O formulário de login, o botão de saída e a navegação de pacientes usam os endpoints reais.

A autorização detalhada por perfil, proteção CSRF dedicada, limitação de tentativas e testes ponta a ponta ainda são pendências antes de qualquer uso em produção.

## Pacientes

- `GET /api/pacientes) e `GET /api/pacientes/{id}`: listagem, pesquisa e consulta autenticadas.
- `POST /api/pacientes): cadastro autenticado; o ID do usuário responsável vem da sessão, nunca do navegador.
- CPF é armazenado normalizado com 11 dígitos e protegido por índice único. Não há validação matemática dos dígitos verificadores, conforme decisão funcional.
- A idade é calculada no servidor a partir da data de nascimento.
- A migração incremental adicionou CPF, nascimento, telefone, e-mail e convênio sem remover colunas antigas.

Edição e exclusão de pacientes ainda estão pendentes. Os demais módulos continuam demonstrativos e serão integrados em etapas posteriores.

## Materiais incorporados

- Interface da Etapa 8: cinco páginas HTML, CSS e JavaScript.
- Modelos de domínio da Etapa 6.
- Testes JUnit da Etapa 7: `PacienteTest`, `ConsultaTest` e `ItemEstoqueTest` (17 métodos).
- Plano de testes e script SQL original.

Fontes: [Etapa 6 e Atividade 7](https://github.com/AnaClaraCruz1/medsinc-refatorado/tree/atividade-7) e [Etapa 8](https://github.com/AnaClaraCruz1/medsinc-web).

## Validações locais registradas em 24/11/2026

Antes das últimas alterações:
- `mvn test): 21 testes executados, zero falhas/erros/ignorados, BUILD SUCCESS.
- `mvn spring-boot:run): servidor iniciado na porta 8080.
- Páginas inicial e consulta abriram pelo servidor.
- API de pacientes retornou 16 registros e pesquisa inexistente retornou zero.

As novas classes, testes, autenticação e cadastro ainda precisam ser executados localmente. Não considerar os novos fluxos validados até a execução posterior.

## Entrega

A conclusão depende de testes integrados, evidências reais, registro de defeitos encontrados e preparação do ZIP final. Usar somente dados fictícios nas capturas e arquivos de entrega.
