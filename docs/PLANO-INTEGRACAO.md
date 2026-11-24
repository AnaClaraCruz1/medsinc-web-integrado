# Plano consolidado de integração — Etapa 9

Este documento distingue implementação de validação. Os testes manuais já aprovados em pacientes não devem ser repetidos sem necessidade de regressão.

## Fontes
- Etapa 6 — https://github.com/AnaClaraCruz1/medsinc-refatorado
- Etapa 7 — testes JUnit e plano de testes incorporados no projeto.
- Etapa 8 — https://github.com/AnaClaraCruz1/medsinc-web

## Implementação
- [x] Criar projeto Maven Spring Boot.
- [x] Incorporar interface da Etapa 8, modelos da Etapa 6 e testes JUnit da Etapa 7.
- [x] Criar migração incremental de pacientes; aplicada no MySQL local e conferida pela usuária.
- [x] Implementar autenticação por sessão, BCrypt e migração de senha legada após login válido.
- [x] Implementar proteção CSRF por token de sessão para login e operações POST, PUT e DELETE.
- [x] Implementar autorização por perfil para estoque, histórico, usuários, relatórios e backups.
- [x] Implementar consulta, pesquisa e cadastro de pacientes via JDBC; ID do usuário responsável vem da sessão.
- [x] Integrar tela de cadastro, consulta, login, logout e contador do painel.
- [x] Implementar API JDBC de consultas e tela integrada, com relacionamento por IDs reais.
- [x] Implementar estoque com listagem, cadastro, edição, exclusão e indicação de nível mínimo.
- [x] Implementar histórico médico com listagem e cadastro relacionados a paciente e médico.
- [x] Implementar gestão de usuários com listagem sem credenciais e cadastro com senha BCrypt.
- [x] Implementar indicadores e registro de relatórios persistidos no MySQL.
- [x] Implementar geração de backup SQL de dados no servidor e histórico de execução.
- [x] Adicionar workflow GitHub Actions para validação de sintaxe JS e execução Maven.
- [ ] Avaliar edição de pacientes; exclusão deve considerar histórico clínico e integridade referencial antes de ser permitida.

## Validação já realizada
- [x] Etapa 6: cinco testes aprovados, conforme histórico do projeto.
- [x] Etapa 7: 17 testes de domínio aprovados, conforme histórico do projeto.
- [x] Etapa 8: páginas e fluxo demonstrativo conferidos manualmente.
- [x] Etapa 9, primeira rodada: 21 testes aprovados antes das alterações dos módulos.
- [x] Etapa 9, pacientes: 29 testes aprovados localmente e 11 verificações manuais aprovadas, incluindo cadastro fictício, CPF duplicado, validações, login e proteção após logout.
- [x] CI do commit consolidado: JavaScript validado com `node --check`; 35 testes Maven aprovados, zero falhas/erros/ignorados, BUILD SUCCESS.
- [ ] Executar uma única validação local consolidada de `mvn clean test`.
- [ ] Executar smoke tests integrados de cada módulo com dados fictícios, incluindo permissões por perfil e token CSRF.
- [ ] Verificar geração, conteúdo e restauração controlada do backup em banco de teste isolado.
- [ ] Conferir se a base local preserva os 16 registros antigos e o paciente fictício ID 20 usado nas evidências.
- [ ] Preparar evidências de testes e versionamento.
- [ ] Preparar ZIP final sem dumps, backups, senhas ou dados pessoais.

## Bugtracking
- [x] Issue #1 criada para a falha real de compilação do diretório de backup, corrigida e encerrada após CI verde.
- [ ] Registrar outros defeitos somente se forem reproduzidos durante a validação consolidada.

## Segurança e entrega
- [x] Dumps de banco e arquivos de backup excluídos da estratégia de versionamento/entrega.
- [x] Senhas não retornadas na listagem de usuários; novas senhas armazenadas com BCrypt.
- [x] Backup gravado em diretório do servidor configurável por `MEDSINC_BACKUP_DIR`.
- [ ] Revisar evidências para garantir que não contenham dados pessoais, CPFs ou credenciais.

## Regra de evidência
Não marcar validação como concluída apenas porque existem arquivos ou endpoints. Cada item de validação exige execução real ou evidência verificável.
