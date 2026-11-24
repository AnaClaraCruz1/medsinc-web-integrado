# Contrato de integração — Pacientes

## Compatibilidade com a Etapa 6

A tabela `pacientes` original mantém `paciente_id`, `nome`, `idade`,
`endereco`, `contato`, `usuario_cadastro_id` e as colunas de auditoria.
A migração `V1__ampliar_dados_paciente.sql` acrescenta CPF, data de nascimento,
telefone, e-mail e convênio. Nenhuma coluna antiga é removida ou renomeada.

Registros antigos continuam consultáveis. Como não há data de nascimento,
CPF ou convênio confiáveis no legado, os campos novos permanecem nulos nesses
registros. A idade legada não deve ser convertida em uma data de nascimento
inventada.

## Regras para o novo cadastro

- Nome: obrigatório, até 55 caracteres no formulário.
- CPF: obrigatório no novo cadastro; normalizar para dígitos e armazenar
  somente com dígitos (11 caracteres). A máscara é apenas de apresentação no navegador. Não aplicar validação matemática dos dígitos.
  Bloquear duplicidade no serviço e por índice UNIQUE no banco.
- Nascimento: obrigatório, data válida e não futura. A idade é calculada
  na camada de serviço no momento da gravação para manter compatibilidade
  com a coluna legada `idade`.
- Telefone e endereço: obrigatórios conforme formulário.
- E-mail: opcional; validar formato quando informado.
- Convênio: obrigatório conforme formulário.
- `usuario_cadastro_id`: deve vir da identidade autenticada no servidor.
  Nunca aceitar esse identificador como dado confiável enviado pelo navegador
  e nunca assumir o ID 1.

## API implementada e pendente

- `POST /api/auth/login`, `GET /api/auth/me` e `POST /api/auth/logout` — autenticação e sessão.
- `GET /api/pacientes` — implementado; lista e aceita `?busca=` para filtrar por nome ou CPF. Exige sessão.
- `GET /api/pacientes/{id}` — implementado; consulta um registro. Exige sessão.
- `POST /api/pacientes` — implementado; cria um registro. Exige sessão e usa o ID do usuário autenticado.
- `PUT /api/pacientes/{id}` — pendente.
- `DELETE /api/pacientes/{id}` — pendente.

As respostas usam DTOs e não expõem entidades JDBC diretamente. As telas de cadastro e consulta consomem os endpoints de pacientes; o fluxo não utiliza localStorage.
A exclusão deve respeitar as FKs existentes: consultas e históricos ligados
ao paciente possuem exclusão em cascata no esquema original; essa consequência
deve ser informada e protegida por autorização antes de habilitar a operação.

## Execução da migração

Fazer backup antes de alterar o banco. Executar o arquivo SQL uma única vez
em um banco já criado com o esquema da Etapa 6. Não executar novamente se
as colunas ou o índice já existirem. Confirmar a versão do MySQL e inspecionar
duplicidades antes de aplicar em uma base que já tenha dados preenchidos em
`cpf` por alguma evolução anterior.
