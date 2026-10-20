# Brasil TV 5.50 — runtime

Data: 2026-10-20. **BLOCKED_BY_DEVICE**; inspeção estática concluída no escopo
acessível do APK protegido. Não confundir evidência estática com tráfego observado.

| Etapa | Evidência |
|---|---|
| Environment | Candidato Fire TV ARM; operador ainda comprará |
| Installation | NÃO EXECUTADA |
| Startup | NÃO EXECUTADO |
| Authentication | UNKNOWN; login futuro pelo operador |
| Session | UNKNOWN |
| Configuration | UNKNOWN |
| Live Catalog | UNKNOWN |
| Channel A | Não selecionado; LIVE_REAL_CHANNEL_A = NOT_RUN |
| Channel B | Não selecionado; LIVE_REAL_CHANNEL_B = NOT_RUN |
| Resource Resolution | UNKNOWN |
| Native Core | ranger-jni presente estaticamente; uso UNKNOWN |
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

Validar dispositivo conforme [ambiente](brasil-tv-5.50-runtime-environment.md),
instalar com participação do operador e observar startup. Depois de login
confirmado pelo operador, registrar apenas sequência, tipos e nomes de campos.
Selecionar dois canais autorizados e correlacionar seleção, recurso, processo,
porta, fonte e primeiro frame. Somente após isso implementar adapter normalizado.

## POC a executar

Cada canal: resolver, primeiro frame e 10 minutos de reprodução no IA TV.
Registrar TTFF, buffering, erros, reconexão, troca, retorno ao catálogo,
background/foreground, expiração e encerramento do recurso anterior.
Nenhuma dessas medições foi realizada. Não preencher PASS com mídia mock.
ADR 003 permanece provisória, sem atualização de decisão por falta de runtime.
