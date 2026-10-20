# Brasil TV 5.2 — compatibility matrix

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
5.50: não localizado nas buscas por APK realizadas; nenhuma comparação inventada.
Dispositivo físico conectado: não usado; todas as ações apontaram ao serial do AVD.

Limite: PASS significa compatibilidade de startup e login, não sessão autenticada,
catálogo acessível ou integração do provider concluída.
