# Inventário e plano de integração

Este checklist evita repetir atividades concluídas anteriormente. Atualizar o status conforme código e evidências forem validados.

## Fontes
- Etapa 6 — https://github.com/AnaClaraCruz1/medsinc-refatorado
- Etapa 8 — https://github.com/AnaClaraCruz1/medsinc-web
- Etapa 7 — testes JUnit e plano de testes preservados no material da Etapa 6.

## Integração

- [x] Criar projeto Maven Spring Boot.
- [x] Incorporar interface da Etapa 8 em resources/static.
- [x] Incorporar modelos de domínio e 17 métodos de teste das etapas anteriores.
- [x] Definir DTO de resposta e endpoints GET de pacientes.
- [x] Criar migração SQL incremental; usuária executou no MySQL local e confirmou as novas colunas e índice único de CPF.
- [x] Implementar autenticação inicial por sessão: login, usuário atual e logout.
- [x] Implementar verificação BCrypt e migração de senha legada para BCrypt após login válido.
- [x] Conectar formulário de login ao endpoint de autenticação, aceitando também identificadores legados armazenados na coluna email.
- [x] Proteger endpoints de leitura e cadastro de pacientes com a sessão.
- [x] Implementar cadastro de pacientes pela API com validação server-side, CPF normalizado/único, idade calculada e usuário responsável derivado da sessão.
- [x] Conectar telas de cadastro e consulta à API; o fluxo de pacientes deixou de usar localStorage.
- [x] Conectar contador do painel à API e implementar logout.
- [x] Exigir sessão nas páginas do painel e dos módulos.
- [ ] Executar localmente os testes recém-adicionados de autenticação e cadastro.
- [ ] Validar login com conta de teste autorizada e verificar /me e logout.
- [ ] Testar cadastro de paciente com dados fictícios, duplicidade de CPF e validações.
- [ ] Implementar edição e exclusão de pacientes pela API.
- [ ] Implementar autorização por perfil e proteção CSRF dedicada.
- [x] Implementar API JDBC de consultas: listagem com nomes relacionados, lista de médicos e cadastro com IDs reais de paciente/profissional.
- [x] Substituir o formulário demonstrativo de consultas por tela conectada à API.
- [x] Adicionar testes unitários para o serviço de consultas (execução local pendente).
- [ ] Integrar estoque, histórico, usuários, relatórios e backups à persistência real.
- [ ] Integrar testes de API/JDBC para fluxos novos.

## Validação e evidências

- [x] Execução inicial local: 21 testes aprovados antes das alterações recentes.
- [x] Inicialização local do Spring Boot na porta 8080.
- [x] Páginas inicial e consulta abriram via servidor.
- [x] GET de pacientes retornou 16 registros; busca inexistente retornou zero antes da exigência de sessão.
- [ ] Executar mvn test após a implementação de consultas (inclui 6 testes novos ainda não executados localmente).
- [ ] Executar os casos aplicáveis do plano de testes da Etapa 7.
- [ ] Registrar defeitos reais no GitHub Issues com reprodução, esperado/obtido e correção.
- [ ] Capturar evidências reais de testes e versionamento.
- [ ] Atualizar documentação e preparar ZIP final.

## Regra de evidência

Não marcar validação como concluída apenas porque existem arquivos. Cada item de validação exige execução real ou evidência verificável.
