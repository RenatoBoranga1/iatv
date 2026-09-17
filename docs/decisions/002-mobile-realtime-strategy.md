# ADR 002 — Realtime futuro

Status: proposta para Milestone 3; nenhuma infraestrutura adicionada no M2.

Preferir WebSocket autenticado via backend relay para controle bidirecional. Uma lease
de activeController por TV, limitada à mesma conta e revogável, previne comandos concorrentes.
SSE é bom para notificações unidirecionais; exige HTTP adicional para comandos. Polling
é fallback de status com intervalo limitado, inadequado para D-Pad responsivo. Conexão
local pode reduzir latência no futuro, mas descoberta de rede não substitui autenticação:
exigir pareamento prévio e canal autenticado. Não abrir controle público da TV na LAN.

Protocolo v1 com allowlist, sequência, commandId idempotente, TTL e ACK; não aceitar
shell, intents Android arbitrárias, URLs livres ou código executável. Revalidar direitos
de conteúdo no destino. Detalhes e fluxos: [arquitetura mobile](../mobile-architecture.md).
