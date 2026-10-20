# Brasil TV 5.50 — runtime

Data: 2026-10-20. **BLOCKED_BY_COMPATIBILITY_GATE**. Instalação e Home
confirmadas no J7, com gate ao acessar Lista de Canais. Mesmo comportamento
relatado pelo operador no SM-T295. Ver [diagnóstico físico](brasil-tv-5.50-device-gate.md).

| Etapa | Evidência |
|---|---|
| Environment | J7 API 27 ARM32 observado; tablet API 30 ARM32/64 relatado; TV pendente |
| Installation | CONFIRMED no J7; SHA-256 instalado igual ao arquivo original |
| Startup | CONFIRMED: HomeActivity ativa no J7 |
| Authentication | Login não exigido segundo operador; mecanismo interno UNKNOWN |
| Session | UNKNOWN |
| Configuration | UNKNOWN |
| Live Catalog | Bloqueado pelo gate; contrato UNKNOWN |
| Channel A | Não selecionado; LIVE_REAL_CHANNEL_A = NOT_RUN |
| Channel B | Não selecionado; LIVE_REAL_CHANNEL_B = NOT_RUN |
| Resource Resolution | UNKNOWN |
| Native Core | Threads ranger/handlerRanger CONFIRMED; Ranger ativo HIGH_CONFIDENCE; mapa de libs UNKNOWN |
| Broker | UNKNOWN |
| Local Gateway | UNKNOWN; strings localhost não comprovam servidor |
| Final Playback Source | UNKNOWN |
| Protocol | UNKNOWN; presença de suporte FFmpeg não confirma fonte |
| MIME | UNKNOWN |
| Headers | UNKNOWN; não persistir valores secretos |
| Expiration | UNKNOWN |
| DRM | UNKNOWN; integração legítima se exigida |
| First Frame | NÃO OBSERVADO |
| Remaining Unknowns | Todos os contratos remotos e lifecycle do core |

## Gate seguinte

Testar o mesmo APK em Android TV/Fire TV com suporte ARM32 verificado e repetir
somente Home → Lista de Canais. Elegibilidade por tipo/modelo é LIKELY;
condição exata e origem local/remota UNKNOWN. Coletar inventário equivalente
no SM-T295 quando disponível. Não contornar o gate nem implementar canais.

## POC futura — fora do escopo desta investigação

Cada canal: resolver, primeiro frame e 10 minutos de reprodução no IA TV.
Registrar TTFF, buffering, erros, reconexão, troca, retorno ao catálogo,
background/foreground, expiração e encerramento do recurso anterior.
Nenhuma dessas medições foi realizada. Não preencher PASS com mídia mock.
ADR 003 permanece provisória, sem atualização de decisão por falta de contratos confirmados.
