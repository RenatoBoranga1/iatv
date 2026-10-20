# Brasil TV — compatibility matrix

## Brasil TV 5.50 — dispositivos físicos — 2026-10-20

| Dispositivo | Android / API | ABI | Install | Startup | Home | Channels | Fonte |
|---|---|---|---|---|---|---|---|
| SM-G610M | 8.1.0 / 27 | armeabi-v7a | PASS | PASS | PASS | BLOCKED_BY_DEVICE_GATE | ADB/UI nesta investigação; CONFIRMED |
| SM-T295 | 11 / 30 | arm64-v8a + armeabi-v7a | PASS | PASS | PASS | BLOCKED_BY_DEVICE_GATE | teste anterior relatado pelo operador; sem coleta simultânea |
| Android TV / Fire TV | UNKNOWN | UNKNOWN | NOT_RUN | NOT_RUN | NOT_RUN | NOT_RUN | validação pendente |

PASS do tablet registra o resultado informado pelo operador, não uma medição
ADB nesta máquina. Nenhuma propriedade adicional do SM-T295 foi inferida.
PASS de instalação/startup/Home não significa catálogo ou playback compatível.
Android TV/Fire TV não declarado compatível. Hipótese TV/box LIKELY;
condição exata do gate UNKNOWN. Ver [diagnóstico](brasil-tv-5.50-device-gate.md).

## Brasil TV 5.2 — histórico de emuladores

Mesmo APK original, hash conferido novamente em 2026-10-20. Sem patches.

| Android | API | ABI | Install | Launch | Crash | Result |
|---|---:|---|---|---|---|---|
| Android TV 14 | 34 | x86 | Success | falha durante onCreate | Long → Integer em PmsHookApplication.invoke | CRASH_SAME |
| Android TV 10 | 29 | x86 | Success, instalação limpa em 20/10 | login visível; segundo launch COLD completo | nenhum crash correspondente no buffer observado | PASS — RUNTIME_UNBLOCKED |

API 34: evidência da sessão anterior reutilizada; instalação limpa e limpeza de
dados reproduziram a mesma assinatura. Segunda abertura/reinstalação posterior
sem captura conclusiva não foram promovidas a PASS. Ao obter sucesso na API 29,
parou-se a investigação de API 34 conforme a regra desta etapa.

API 29: primeiro PID permaneceu o mesmo entre observações do formulário; após
force-stop, novo PID permaneceu ativo e o mesmo formulário foi confirmado.
`am start -W`: Status ok, LaunchState COLD, Complete, TotalTime 10374 ms.
Esse tempo mede launch, não time-to-first-frame de vídeo. Helper estrutural em
20/10 13:34 UTC confirmou processo ativo, formulário presente, campos vazios e
senha mascarada. Não houve autenticação enviada nem teste prolongado.

APIs 30–33: não testadas. Imagens adicionais não foram baixadas.
Naquela investigação da 5.2, a 5.50 ainda não havia sido localizada; os resultados
posteriores da 5.50 estão na seção de dispositivos físicos acima.
Dispositivo físico conectado: não usado; todas as ações apontaram ao serial do AVD.

Limite: PASS significa compatibilidade de startup e login, não sessão autenticada,
catálogo acessível ou integração do provider concluída.
