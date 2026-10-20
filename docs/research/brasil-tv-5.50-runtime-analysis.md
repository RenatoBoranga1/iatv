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

## Samsung SM-G610M — device compatibility gate

**CONFIRMED (observação direta):** SM-G610M, Android 8.1.0/API 27,
armeabi-v7a; APK 5.50 instalado com SHA-256
`DD7F35A5949BECE00704BDF7D9D7A7431945F2DA5A78F873564061A38F81BA88`,
igual ao original. Instalação concluída, startup e HomeActivity/processo ativos.
Ao acessar Lista de Canais, a UI mostra:
“Seu dispositivo não é compatível com este aplicativo.”

Perfil phone, ausência de Leanback/television, threads ranger/handlerRanger
observadas e recusas SELinux ao ler estatísticas do sistema são CONFIRMED.
Nenhum erro de linker ou crash foi observado nos buffers consultados;
isso não prova ausência em toda a execução.

**HIGH_CONFIDENCE:** gate posterior ao startup; não é rejeição de instalação
por minSdk ou ABI. **LIKELY:** restrição por tipo/modelo/form factor,
possivelmente exigência de TV/box. Não promover essa hipótese a CONFIRMED.

**UNKNOWN:** condição exata do gate, relação causal das recusas SELinux,
DRM completo, carregamento completo das bibliotecas e aceitação de Android TV/Fire TV.

Comparação: SM-T295, Android 11/API 30, arm64-v8a + armeabi-v7a, também instalou,
iniciou, mostrou Home e a mesma mensagem segundo teste anterior do operador.
Não houve coleta simultânea do tablet nesta máquina; propriedades adicionais
permanecem desconhecidas. Ver [matriz](brasil-tv-compatibility-matrix.md).

Esta retomada é exclusivamente documental. Testes de aplicação não reexecutados;
provider fail-closed e mock preservados. Revisão por diff, links e escopo de arquivos.

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
