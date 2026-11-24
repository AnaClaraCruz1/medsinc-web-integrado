# Evidência de validação automatizada — 24/11/2026

## GitHub Actions

- Workflow: Build and test
- Execução: https://github.com/AnaClaraCruz1/medsinc-web-integrado/actions/runs/45498247773
- Commit validado: `d0494026cc94a49554d8de46a3285b0f7c94bf49`
- Ambiente: Ubuntu, Temurin Java 17 e Node.js 22
- Resultado: sucesso

## Etapas executadas

1. Checkout do repositório.
2. Configuração do JDK 17.
3. Configuração do Node.js 22.
4. Verificação sintática de `src/main/resources/static/assets/js/app.js` com `node --check`.
5. Verificação sintática de `src/main/resources/static/assets/js/modules.js` com `node --check`.
6. Execução de `mvn --batch-mode --no-transfer-progress clean test`.

## Resultado dos testes

- Testes executados: 36
- Falhas: 0
- Erros: 0
- Ignorados: 0
- Build: `BUILD SUCCESS`

Distribuição registrada:
- AutenticacaoWebServiceTest: 4
- PacienteConsultaServiceTest: 4
- ConsultaServiceTest: 7
- PacienteCadastroServiceTest: 4
- ConsultaTest: 4
- PacienteTest: 5
- ItemEstoqueTest: 8

## Bugtracking

- Issue #1 — https://github.com/AnaClaraCruz1/medsinc-web-integrado/issues/1 — falha de compilação no serviço de backup; corrigida e encerrada.
- Issue #2 — https://github.com/AnaClaraCruz1/medsinc-web-integrado/issues/2 — trecho Java inválido no serviço de consultas; corrigido e encerrado.

## Limite desta evidência

Esta execução comprova compilação, testes automatizados e sintaxe dos dois arquivos JavaScript no ambiente da CI. Não substitui a execução local conectada ao MySQL nem os testes manuais de interface, permissões, CSRF e geração/restauração de backup. Esses itens permanecem no plano de integração.
