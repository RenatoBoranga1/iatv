# Milestone 2 — baseline

Inspeção concluída em 17/09/2026 antes de alterar código. Base: `406163f`.
README, docs, fontes Android/backend/admin, contratos, Prisma, testes e CI revisados.

## Validação inicial

| Comando | Resultado |
| --- | --- |
| npm run build | Contratos e backend aprovados |
| npm run lint | Aprovado |
| npm test | 7 testes aprovados |
| npm run build -w @iatv/admin | Aprovado, página estática |
| gradlew assembleDebug testDebugUnitTest lintDebug | BUILD SUCCESSFUL; tarefas incrementais, 1 teste existente; 0 erros e 17 warnings no relatório |
| GitHub Actions M1 | Backend e Android aprovados no run 35123977361 (edb282a) |
| npm audit --omit=dev | 13 alertas: 11 high, 2 moderate; inclui CLI Prisma transitivo |

Ambiente: Windows, Node 24, JDK 21, SDK 35. CI usa Node 22 e PostgreSQL 17.
Dependências iniciais: Nest 11.1.6, Swagger 11.2.0, Prisma 6.19.0, Next 16.3.5,
React 19.2.4, Kotlin 2.1.20, AGP 8.10.1, Gradle 8.11.1, Media3 1.6.1,
Compose BOM 2025.05.01, TV Material 1.0.0, Retrofit 2.11.0, Hilt 2.56.2.

## Estado e riscos

- Arquitetura adequada: monorepo, API Nest/Prisma, Android Compose/Hilt/Media3; manter.
- Player criado na composição, somente pausa em ON_STOP; sem recuperação limitada ou telemetria.
- Foco e scroll parcialmente preservados; detalhe/player não sobrevivem recriação.
- Todos os erros de HTTP/JSON viram offline; timeouts implícitos.
- Progresso local salva apenas posição; sem data, ordenação ou estado explícito de conclusão.
- Catálogo fictício de 8 itens; sem imagens remotas, autenticação, refresh tokens ou sincronização.
- Admin é estrutura estática, não CRUD. Não inventar endpoints de conta neste milestone.
- API já versionada em /v1; logs sem requestId, formato de erro inconsistente.
- Warnings Android: versões disponíveis, target SDK, banner vetorial grande, orientação TV,
  regras de backup e sugestões KTX. Não suprimir para mascarar problemas.
- Busca de segredos nos arquivos versionados encontrou apenas senha exemplo e documentação;
  nenhuma .env, chave de assinatura ou local.properties versionada. Ampliar .gitignore.
- Docker e dispositivo físico ainda não testados nesta etapa. Resultados M1 não são testes M2.

## Decisões para implementação

Melhorias incrementais de foco, lifecycle, estados, retry 2/4/8 segundos, progresso local
com conclusão em 95%, conectividade, logs debug sanitizados e diagnóstico. Manter API /v1.
Atualizar dependências somente com justificativa de segurança e testes. Não adicionar
Detekt e Ktlint simultaneamente: lint Android e testes direcionados são suficientes aqui.
Preparar contratos e documentação mobile, sem implementar pairing/controle ou autenticação.
Usar mídia própria. Validação em TV física e sessão de duas horas terão gate explícito.
