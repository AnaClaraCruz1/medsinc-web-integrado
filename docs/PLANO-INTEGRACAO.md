# Inventário e plano de integração

Este checklist evita repetir atividades concluídas anteriormente. Atualizar o status conforme código e evidências forem validados.

## Fontes
- [Etapa 6 — medsinc-refatorado](https://github.com/AnaClaraCruz1/medsinc-refatorado)
- [Etapa 8 — medsinc-web](https://github.com/AnaClaraCruz1/medsinc-web)
- Etapa 7 — testes JUnit e plano de testes preservados no material da Etapa 6.

## Integração

- [x] Criar projeto Maven Spring Boot.
- [x] Incorporar interface da Etapa 8 em `resources/static`.
- [x] Incorporar modelos de domínio e 17 métodos de teste das etapas anteriores.
- [x] Definir DTO de resposta e endpoints GET de pacientes.
- [x] Criar migração SQL incremental; usuária executou no MySQL local e confirmou as novas colunas e índice único de CPF.
- [x] Implementar autenticação inicial por sessão: login, usuário atual e logout.
- [x] Implementar verificação BCrypt e migração de senha legada para BCrypt após login válido.
- [x] Conectar formulário de login ao endpoint de autenticação.
- [ ] Executar localmente os testes recém-adicionados de autenticação.
- [ ] Validar login com conta de teste autorizada e verificar /me e logout.
- [ ] Proteger páginas e endpoints por autenticação e autorização de perfil.
- [ ] Implementar cadastro, edição e exclusão de pacientes pela API.
- [ ] Conectar telas de cadastro e consulta à API; retirar uso de localStorage no fluxo de pacientes.
- [ ] Integrar consultas, estoque, histórico, usuários, relatórios e backups à persistência real.
- [ ] Integrar testes de API/JDBC para fluxos novos.

## Validação e evidências

- [x] Execução inicial local: 21 testes aprovados antes da implementação de autenticação.
- [x] Inicialização local do Spring Boot na porta 8080.
- [x] Páginas inicial e consulta abertas via servidor.
- [x] GET de pacientes retornou 16 registros; busca inexistente retornou zero.
- [ ] Reexecutar `mvn test` após as alterações de autenticação.
- [ ] Executar os casos aplicáveis do plano de testes da Etapa 7.
- [ ] Registrar defeitos reais no GitHub Issues com reprodução, esperado/obtido e correção.
- [ ] Capturar evidências reais de testes e versionamento.
- [ ] Atualizar documentação e preparar ZIP final.

## Regra de evidência

Não marcar validação como concluída apenas porque existem arquivos. Cada item de validação exige execução real ou evidência verificável.
