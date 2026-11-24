# Inventário e plano de integração

Este checklist evita repetir atividades já concluídas nas etapas anteriores. Atualizar o status e registrar evidências conforme a integração avançar.

## Fontes de referência

- [Etapa 6 — medsinc-refatorado](https://github.com/AnaClaraCruz1/medsinc-refatorado)
- [Etapa 8 — medsinc-web](https://github.com/AnaClaraCruz1/medsinc-web)
- Etapa 7 — testes JUnit e plano de testes mantidos no material/repositório da Etapa 6.

## Inventário a confirmar antes de copiar

- [ ] Confirmar branch e versão final do código refatorado da Etapa 6.
- [ ] Localizar o plano de testes e as evidências originais da Etapa 7.
- [ ] Localizar as classes de teste JUnit (17 testes já aprovados).
- [ ] Copiar as cinco páginas HTML e os recursos CSS/JavaScript da Etapa 8.
- [ ] Conferir o script SQL e identificar diferenças entre o esquema e os formulários web.
- [ ] Registrar quais módulos já possuem teste automatizado e quais precisam de cobertura nova.

## Integração

- [ ] Criar projeto Maven Spring Boot.
- [ ] Incorporar modelos, serviços e repositórios compatíveis da Etapa 6.
- [ ] Definir DTOs e endpoints REST.
- [ ] Definir migração compatível do banco sem perda de dados.
- [ ] Integrar autenticação e autorização.
- [ ] Servir a interface da Etapa 8 em `resources/static`.
- [ ] Substituir operações demonstrativas por chamadas reais à API.
- [ ] Integrar os 17 testes existentes sem reduzir cobertura.
- [ ] Criar testes novos apenas para funcionalidades e integrações ainda não cobertas.

## Validação e evidências

- [ ] Executar os testes integrados e registrar comando, data, resultado e versão.
- [ ] Executar os casos do plano de testes da Etapa 7 aplicáveis à versão web.
- [ ] Registrar defeitos no GitHub Issues, com passos para reproduzir, resultado esperado/obtido e correção.
- [ ] Capturar evidências reais dos testes e do versionamento.
- [ ] Atualizar README e preparar ZIP final do projeto.

## Regra de evidência

Não marcar como concluído com base apenas na existência de arquivos. Cada item de validação deve apontar para execução real ou evidência verificável.
