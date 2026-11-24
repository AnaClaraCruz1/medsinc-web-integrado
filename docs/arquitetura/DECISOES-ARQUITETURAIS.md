# Arquitetura — MEDSiNC Web Integrado

## Decisão arquitetural
Aplicação Spring Boot com API REST e interface estática servida pelo próprio Spring Boot. O navegador consome endpoints JSON por `fetch()`, mantendo a interface da Etapa 8 no mesmo servidor.

## Camadas
| Camada | Responsabilidade |
|---|---|
| `api/controller` | Endpoints REST, códigos HTTP e validação de entrada |
| `api/dto` | Contratos de requisição e resposta |
| `application/service` | Regras de negócio e casos de uso |
| `infrastructure/repository` | Persistência JDBC |
| `infrastructure/security` | Filtro de proteção CSRF |
| `domain/model` | Modelos de domínio |
| `resources/static` | HTML, CSS e JavaScript |
| `resources/db` | Migrações SQL manuais |

## Regras de integração
1. Reaproveitar componentes validados da Etapa 6 e preservar os 17 métodos de teste da Etapa 7.
2. Manter a interface da Etapa 8 e consumir os dados pela API; os módulos integrados não dependem de localStorage para persistência.
3. Usar DTOs e projeções; não expor credenciais ou hashes.
4. Consultas e histórico trafegam com IDs relacionais, mesmo quando a interface exibe nomes.
5. Identidade autenticada determina os campos de auditoria; nunca fixar IDs de usuário no navegador.
6. Migrações são manuais e devem ser aplicadas uma única vez conforme instruções.

## Pacientes
A migração incremental adicionou CPF, nascimento, telefone, e-mail e convênio, mantendo colunas antigas e registros existentes. CPF é normalizado e único; a validação matemática dos dígitos não é aplicada, conforme decisão funcional. A idade é calculada no servidor a partir da data de nascimento. Registros legados podem continuar com os novos campos nulos.

## Sessão e segurança
- Login, usuário atual e logout são implementados em `/api/auth`.
- Senhas legadas são comparadas em tempo constante e migradas para BCrypt após autenticação válida.
- Novas senhas também são gravadas com BCrypt.
- Cookie de sessão HttpOnly e SameSite=Lax.
- `GET /api/auth/csrf` cria/retorna token da sessão; o filtro exige `X-CSRF-TOKEN` em POST, PUT e DELETE, inclusive login; somente a emissão do token é isenta.
- Autorização por perfil: estoque (administrador/farmacêutico), histórico (administrador/médico), usuários e backups (administrador), relatórios (administrador/técnico).
- As rotas de pacientes e consultas exigem sessão autenticada.

## Persistência por módulo
- Pacientes: `PacienteJdbcRepository`.
- Consultas: `ConsultaJdbcRepository`, com relacionamento para pacientes e usuários médicos.
- Estoque, histórico, usuários, relatórios e backups: `GestaoJdbcRepository`.
- Backup: `BackupBancoService` gera arquivo SQL de dados no servidor, sem comando destrutivo de exclusão; registros existentes são atualizados com `INSERT ... ON DUPLICATE KEY UPDATE`, sem excluir e reinserir as linhas. O esquema precisa existir no banco de destino.

## Cuidados com dados
- Credenciais somente por variáveis de ambiente.
- Dumps e arquivos de backup contêm dados pessoais e hashes de senha; não versionar nem anexar à entrega acadêmica.
- Usar dados fictícios em testes e capturas.
- Não utilizar o sistema como serviço clínico real ou ambiente de produção sem revisão de segurança, privacidade e conformidade.

## Critério de conclusão
Código implementado não equivale a validação. Cada fluxo precisa passar por testes automatizados, execução integrada e evidência correspondente. O plano de integração mantém separados os itens implementados dos itens efetivamente validados.
