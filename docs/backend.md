# Backend

NestJS, REST versionado `/v1`, Swagger `/docs` e documento `/docs-json`. Compilar com `tsc` preserva os metadados dos decorators usados pela validação NestJS. O modo dev compila ao iniciar; alterações TypeScript exigem reiniciar o comando.

Rotas: `GET health`, `home`, `catalog`, `catalog/:id`, `search?q=`, `sports?date=YYYY-MM-DD`, `live/categories`, `live/:id/epg`, `playback/:id`, `config`; `POST ai/chat` com `{ "message": "Quero uma comédia" }`.

O seed cria oito itens fictícios e flags iniciais. Execute migrations antes de seed. PostgreSQL indisponível interrompe o boot no modo padrão. Rate limit: 120 requisições por minuto/IP; os limites em memória são adequados para uma instância de demonstração.

Não existem endpoints de conta ou administração. O catálogo público fictício não requer autenticação. Antes de adicionar informações pessoais ou operações administrativas: autenticação, JWT curto, refresh com rotação, hashing seguro e auditoria.
