# Arquitetura e decisões

Milestone 1: cliente Android TV → API REST NestJS → catálogo PostgreSQL via Prisma. Agenda, EPG e assistente são providers mock determinísticos. Nenhuma chamada a serviços de conteúdo externos.

## Decisões
- Monorepo npm para contratos/backend/admin; Gradle independente para Android.
- Kotlin + Compose + componentes Compose for TV, Hilt e Retrofit. Media3 gerencia a reprodução e seu ciclo de vida.
- Contrato compacto `ContentCard` permite os mesmos carrosséis para filmes, séries, canais e eventos. O esquema desta fase contém `Content` e `FeatureFlag`; não criar dezenas de tabelas sem fluxos implementados. Modelos especializados, contas e relações completas ficam para a próxima fase.
- PostgreSQL é o modo padrão. `DATA_MODE=mock` é uma alternativa explícita de desenvolvimento sem persistência. Falhas do banco nunca ativam o mock silenciosamente.
- Catálogo fictício com seed idempotente. Agenda calculada pela data solicitada em America/Sao_Paulo. Não usar clubes reais ou presumir direitos de transmissão.
- `MockAiProvider` → `AuthorizedTools` → serviços. Sem SDK de IA real, SQL arbitrário ou credenciais no APK.
- Favoritos, cache da última Home e retomada são locais via SharedPreferences no perfil demonstrativo. Não há sincronização, login ou multiusuário nesta fase.
- Toda fonte de reprodução vem de `/v1/playback/:id`. A mídia é um sinal audiovisual gerado pelo próprio projeto. Canais repetem o MP4, explicitamente como live simulado; não há ingestão HLS ao vivo nesta fase.
- HTTP permitido somente no APK debug para rede local. Uma futura versão release exige API HTTPS.
- Admin é uma página estrutural Next.js sem CRUD, autenticação ou operações sensíveis.
- Dependências fixadas e lockfile versionado. Prisma 6 foi escolhido para um fluxo convencional de migrations PostgreSQL; atualização de major será tratada separadamente.

## Fluxo
1. TV carrega `/v1/home` com seções e cards.
2. OK abre detalhes; playback resolve uma URL no backend.
3. Busca agrega o catálogo por tipo; assistente devolve mensagem + cards no mesmo contrato.
4. TV salva favoritos/progresso apenas no armazenamento local do app.

Clientes futuros `apps/tizen` e `apps/webos` consumirão esta API; não estão implementados.
