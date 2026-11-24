# Arquitetura inicial — MEDSiNC Web Integrado

## Decisão arquitetural

A solução será organizada como uma aplicação Spring Boot com API REST e interface estática servida pelo próprio Spring Boot. O navegador consumirá os endpoints JSON por `fetch()`. Essa abordagem mantém a interface da Etapa 8 e evita introduzir um segundo servidor web durante a entrega.

## Camadas

| Camada | Responsabilidade |
|---|---|
| `controller` | Endpoints REST, códigos HTTP e validação de entrada |
| `dto` | Contratos de requisição e resposta; não expor entidades JDBC diretamente |
| `service` | Regras de negócio e coordenação dos casos de uso |
| `repository` | Contratos de persistência |
| `repository.jdbc` | Implementações JDBC e mapeamento SQL |
| `model` | Entidades e enums de domínio |
| `config` | Configuração de banco, CORS e segurança |
| `exception` | Exceções de negócio e respostas padronizadas |
| `resources/static` | HTML, CSS e JavaScript da Etapa 8 |
| `resources/db` | Script/migrações e dados estritamente fictícios de desenvolvimento |

## Regras de integração

1. Reaproveitar os componentes de domínio, serviços, contratos de repositório e JDBC da Etapa 6 quando compatíveis; adaptar com mudanças pequenas e justificadas.
2. Preservar os testes JUnit da Etapa 7 e executá-los no projeto integrado. Não substituir testes aprovados por cópias com menor cobertura.
3. Manter os arquivos da Etapa 8 como base visual; substituir progressivamente o armazenamento demonstrativo em `localStorage` por chamadas à API.
4. Usar DTOs para desacoplar o formato do formulário do esquema relacional.
5. Consultas e histórico devem trafegar com identificadores relacionais, mesmo quando a interface exibir nomes.
6. A identidade do usuário autenticado deve determinar campos de auditoria como `atualizado_por`; não fixar IDs no código.
7. Não expor credenciais, hashes de senha ou dados pessoais em respostas, logs ou evidências.

## Pontos que exigem compatibilização

- O formulário web de paciente contém CPF, data de nascimento, telefone, e-mail e convênio, enquanto o modelo/tabela original da Etapa 6 contém um conjunto menor de campos. A decisão de persistência deverá preservar os dados existentes e documentar a migração.
- O front-end bloqueia CPF duplicado, mas não valida matematicamente os dígitos verificadores. Essa regra deve permanecer coerente com a decisão funcional já tomada.
- A autenticação demonstrativa da interface deverá ser substituída por autenticação real; a senha será armazenada com hash apropriado.
- Estoque, consultas, relatórios e backups precisam ser ligados à persistência real ou ter seu escopo explicitamente delimitado na documentação.

## Segurança

- Configurar a conexão exclusivamente por variáveis de ambiente.
- Não versionar arquivos locais de configuração ou credenciais.
- Aplicar hash de senha (por exemplo, BCrypt) e não registrar senhas em logs.
- Validar entradas no servidor, mesmo que o navegador também valide.
- Definir autorização por perfil antes de liberar operações administrativas.
- Usar dados fictícios em testes e capturas de tela.

## Critérios de conclusão

A arquitetura só será considerada implementada quando houver código, testes executados e evidências correspondentes. Este documento registra decisões iniciais, não certifica funcionalidades concluídas.
