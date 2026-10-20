# Provider contract matrix
## Escopo atual: 5.50 / Milestone 2.5.2

5.50 é com.interactive.brasiliptv; a pesquisa de 5.2 abaixo é histórica.
APK validado estaticamente, runtime **BLOCKED_BY_DEVICE**. Nenhum contrato
remoto da 5.50 foi confirmado. Os estados da matriz permanecem pendentes.
Live tem prioridade; filmes/séries ficam fora desta etapa.

## Histórico 5.2

Atualização 20/10: login acessível na API 29; isso não revela contratos de rede.
Auth UI é CONFIRMED; os itens desta matriz permanecem REQUIRES_PROVIDER_CONTRACT.
Todos os contratos abaixo precisam de evidência de runtime ou documentação oficial
do provider. Símbolos não definem o protocolo de rede.

| Contract | Estado | Evidência necessária |
|---|---|---|
| Auth URL | REQUIRES_PROVIDER_CONTRACT | schema/trace sanitizado autorizado |
| Auth request | REQUIRES_PROVIDER_CONTRACT | schema/trace sanitizado autorizado |
| Auth response | REQUIRES_PROVIDER_CONTRACT | schema/trace sanitizado autorizado |
| Device identity | REQUIRES_PROVIDER_CONTRACT | schema/trace sanitizado autorizado |
| Broker request | REQUIRES_PROVIDER_CONTRACT | schema/trace sanitizado autorizado |
| Broker response | REQUIRES_PROVIDER_CONTRACT | schema/trace sanitizado autorizado |
| Live categories | REQUIRES_PROVIDER_CONTRACT | schema/trace sanitizado autorizado |
| Live channels | REQUIRES_PROVIDER_CONTRACT | schema/trace sanitizado autorizado |
| EPG | REQUIRES_PROVIDER_CONTRACT | schema/trace sanitizado autorizado |
| Movies | REQUIRES_PROVIDER_CONTRACT | schema/trace sanitizado autorizado |
| Series | REQUIRES_PROVIDER_CONTRACT | schema/trace sanitizado autorizado |
| Seasons | REQUIRES_PROVIDER_CONTRACT | schema/trace sanitizado autorizado |
| Episodes | REQUIRES_PROVIDER_CONTRACT | schema/trace sanitizado autorizado |
| Playback resource | REQUIRES_PROVIDER_CONTRACT | schema/trace sanitizado autorizado |
| Playback response | REQUIRES_PROVIDER_CONTRACT | schema/trace sanitizado autorizado |
| Protocol | REQUIRES_PROVIDER_CONTRACT | schema/trace sanitizado autorizado |
| Quality | REQUIRES_PROVIDER_CONTRACT | schema/trace sanitizado autorizado |
| Audio | REQUIRES_PROVIDER_CONTRACT | schema/trace sanitizado autorizado |
| Subtitle | REQUIRES_PROVIDER_CONTRACT | schema/trace sanitizado autorizado |
| DRM | REQUIRES_PROVIDER_CONTRACT | schema/trace sanitizado autorizado |
| Geo restriction | REQUIRES_PROVIDER_CONTRACT | schema/trace sanitizado autorizado |

Não mapear nomes de campos por suposição. ContentProvider/PlaybackSource são
contratos internos IA TV; não alegam correspondência com payloads legados.

