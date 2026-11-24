# Plano de Testes — Medsinc | Atividade 7

## 1. Objetivo

Definir verificações unitárias e manuais para avaliar as regras de negócio e os principais fluxos do Medsinc. O plano considera o código Java refatorado na etapa anterior e os requisitos já identificados para a evolução do sistema web.

## 2. Escopo

**Testes unitários:** regras de domínio que podem ser verificadas sem conexão com banco de dados, especialmente validações de paciente, consulta e estoque.

**Testes manuais:** fluxos funcionais de cadastro, consulta, histórico médico, estoque, autenticação, permissões e persistência. Os cenários identificados como evolução web são planejados para execução quando essas telas e funcionalidades estiverem disponíveis; não significam que o sistema web já esteja implementado.

**Fora do escopo desta atividade:** teste de carga, teste de segurança especializado, teste de invasão e homologação em produção.

## 3. Ambiente e recursos

- Sistema operacional: Windows 11.
- IDE: Apache NetBeans.
- Linguagem: Java 17.
- Gerenciador de dependências: Maven.
- Framework de testes: JUnit Jupiter 5.
- Banco de dados: MySQL, somente nos testes manuais que exigem persistência.
- Massa de dados: registros fictícios, sem utilização de dados reais de pacientes.

## 4. Estratégia de testes unitários

Os testes unitários são automatizados com JUnit 5 e executados sem conexão ao MySQL. O projeto dedicado está em `03_PROJETO_TESTES_JUNIT`.

| ID | Classe/regra | Cenário | Resultado esperado |
|---|---|---|---|
| TU-01 | ItemEstoque | Criar item com dados válidos | Objeto criado com os valores informados |
| TU-02 | ItemEstoque | Nome nulo ou em branco | Lançar IllegalArgumentException |
| TU-03 | ItemEstoque | Quantidade negativa | Lançar IllegalArgumentException |
| TU-04 | ItemEstoque | Quantidade mínima negativa | Lançar IllegalArgumentException |
| TU-05 | ItemEstoque | Usuário responsável inválido | Lançar IllegalArgumentException |
| TU-06 | ItemEstoque | Quantidade igual ao mínimo | Identificar alerta de estoque |
| TU-07 | ItemEstoque | Quantidade abaixo do mínimo | Identificar alerta de estoque |
| TU-08 | ItemEstoque | Quantidade acima do mínimo | Não identificar alerta |
| TU-09 | Paciente | Criar paciente válido | Objeto criado corretamente |
| TU-10 | Paciente | Nome ausente | Lançar IllegalArgumentException |
| TU-11 | Paciente | Idade negativa | Lançar IllegalArgumentException |
| TU-12 | Paciente | Usuário responsável inválido | Lançar IllegalArgumentException |
| TU-13 | Paciente | Gerar cópia sem ID | Cópia mantém dados e fica sem identificador |
| TU-14 | Consulta | Criar consulta válida | Objeto criado corretamente |
| TU-15 | Consulta | Paciente ou médico inválido | Lançar IllegalArgumentException |
| TU-16 | Consulta | Data, horário ou status ausente | Lançar IllegalArgumentException |

## 5. Plano de testes manuais

Os cenários abaixo devem ser executados em ambiente de desenvolvimento ou homologação, com usuário e dados fictícios. Registrar o resultado real como **Aprovado**, **Reprovado** ou **Não executado**, incluindo observações e evidência quando aplicável.

| ID | Funcionalidade | Procedimento | Resultado esperado |
|---|---|---|---|
| TM-01 | Autenticação | Informar credenciais válidas | Acesso permitido conforme perfil |
| TM-02 | Autenticação | Informar senha incorreta | Acesso negado e mensagem adequada |
| TM-03 | Permissões | Acessar módulo não permitido ao perfil | Acesso bloqueado |
| TM-04 | Pacientes | Cadastrar paciente com dados válidos | Registro salvo e exibido na listagem |
| TM-05 | Pacientes | Tentar salvar sem nome ou com idade inválida | Sistema impede gravação e informa o problema |
| TM-06 | Pacientes | Pesquisar por nome ou identificador disponível | Exibir registros correspondentes |
| TM-07 | Consultas | Agendar consulta com paciente, médico, data e hora válidos | Consulta registrada corretamente |
| TM-08 | Consultas | Tentar agendar sem dados obrigatórios | Sistema impede o agendamento |
| TM-09 | Consultas | Alterar status para realizada ou cancelada | Status atualizado e apresentado corretamente |
| TM-10 | Histórico médico | Registrar descrição para paciente válido | Registro associado ao paciente correto |
| TM-11 | Histórico médico | Tentar salvar descrição vazia | Sistema impede gravação |
| TM-12 | Estoque | Cadastrar item com quantidade válida | Item salvo e exibido na listagem |
| TM-13 | Estoque | Informar quantidade negativa | Sistema impede gravação |
| TM-14 | Estoque | Reduzir quantidade até o mínimo | Sistema sinaliza necessidade de reposição |
| TM-15 | Estoque | Pesquisar item e excluir registro permitido | Resultado correto e exclusão refletida |
| TM-16 | Persistência | Salvar registro e consultar novamente | Dados permanecem após atualizar/reabrir a tela |
| TM-17 | Persistência | Tentar operação com dados inválidos | Banco não recebe registro inválido |
| TM-18 | Navegação web (planejado) | Abrir páginas e navegar pelos módulos | Páginas carregam sem erro e menus direcionam corretamente |
| TM-19 | Responsividade web (planejado) | Acessar em desktop, tablet e celular | Conteúdo permanece legível e utilizável |
| TM-20 | Sessão web (planejado) | Encerrar sessão e tentar acessar rota protegida | Usuário é redirecionado para autenticação |

## 6. Critérios de aprovação

- Todos os testes unitários devem finalizar sem falhas.
- Validações devem impedir dados inválidos antes da persistência.
- Operações válidas devem refletir corretamente nas consultas e listagens.
- O acesso deve respeitar o perfil autenticado.
- Os cenários planejados para o sistema web só serão considerados aprovados após implementação e execução.
- Falhas devem ser registradas com ID do teste, passos para reprodução, resultado esperado, resultado obtido e evidência.

## 7. Registro dos resultados

Para cada execução manual, preencher:

- **ID do teste:**
- **Data da execução:**
- **Responsável:**
- **Ambiente/versão:**
- **Resultado:** Aprovado / Reprovado / Não executado
- **Observações:**
- **Evidência:**

Não registrar como executado um cenário que ainda não foi testado. Os testes de integração com banco devem ser identificados separadamente dos testes unitários.

## 8. Referência técnica

A implementação utiliza JUnit Jupiter e Maven Surefire para descoberta e execução dos testes. A documentação oficial do JUnit está disponível em https://docs.junit.org/5.10.2/user-guide/.
