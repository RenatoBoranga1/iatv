# Provider evidence ledger
CONFIRMED = evidência direta indicada; HIGH_CONFIDENCE = inferência forte;
LIKELY = hipótese; UNKNOWN = sem evidência suficiente. Separar existência de execução.

| Finding | Evidence | Confidence | Implication | Next Test | Status |
|---|---|---|---|---|---|
| APK 5.2 identificado | aapt, hash no relatório estático | CONFIRMED | Base reprodutível | comparar 5.50 | observado |
| DEX pequeno + payload binário | ZIP, 13.984/1.972.372 bytes | CONFIRMED | bootstrap separado | observar classes carregadas | estático |
| Proteção/carregamento posterior | estrutura do APK | HIGH_CONFIDENCE | DEX não contém toda lógica | runtime | hipótese |
| TVCore setters e lifecycle existem | strings Java_com_tvbus_engine_TVCore em libtvcore x86 | CONFIRMED | pontos de observação | sequência real | estático |
| Porta local configurável | setPlayPort/getPlayPort | CONFIRMED | possível saída local | correlacionar socket e URI | estático |
| Gateway HTTP local | apenas nomes de métodos | LIKELY | não escolher arquitetura ainda | socket + URI + MIME | não confirmado |
| CAuth e broker existem | símbolos C++ em libtvcore | CONFIRMED | possíveis pontos de contrato | observar request/response | estático |
| Device fields existem | jni_device_* | CONFIRMED | possível binding | instalação/relaunch | estático |
| Auth → broker → peers → gateway | hipótese do escopo | LIKELY | orientar investigação | trace de duas seleções | não confirmado |
| Channel ID → resource | nenhum catálogo observado | UNKNOWN | impede mapper | dois canais autorizados | pendente |
| Sessão/token/VIP | nenhum login observado | UNKNOWN | impede auth/entitlement | conta de teste | pendente |
| URI/formato final | nenhum frame real observado | UNKNOWN | impede resolver | execução autorizada | pendente |

| Inicialização falha na API 34 x86 | ClassCastException Long→Integer, SopCast.onCreate(MyApplication:18), 21/09 | CONFIRMED | bloqueia runtime autenticado | repetir em dispositivo compatível/API 29 | BLOCKED_BY_RUNTIME_ENVIRONMENT |
| Cast localizado no proxy PackageManager | PmsHookApplication.invoke:37 → Proxy.getPackageInfo, trace sanitizado | CONFIRMED | investigar flags da API interna | comparação API 29 | contexto identificado |
| Hook espera flags Integer e recebe Long moderno | stack + AOSP Android 10 int / Android 13 long + sucesso API 29 | HIGH_CONFIDENCE | causa de compatibilidade provável | corpo do hook só se necessário futuramente | sem patch |
| Limpar dados não resolve API 34 | fresh install e pm clear reproduzem assinatura | CONFIRMED | persistência antiga não é explicação sustentada | usar ambiente compatível | observado |
| API 29 executa mesmo APK e mostra login | install Success, UI com senha mascarada, launch COLD ok/Complete | CONFIRMED | runtime de pesquisa utilizável | sessão de teste | RUNTIME_UNBLOCKED |
| Login permanece após relaunch | processo ativo + helper login_form_present=true | CONFIRMED | fluxo de UI reproduzível | auth normal | observado |
| Catálogo / CAuth / TVCore em execução | sem sessão submetida ou probe de métodos | UNKNOWN | não implementar contrato por inferência | conta de teste e instrumentação sanitizada | BLOCKED_BY_PROVIDER_INFORMATION |
# Escopo 5.50 — 2026-10-20

| Evidência | Classificação | Limite |
|---|---|---|
| SHA256 DD7F35A5949BECE00704BDF7D9D7A7431945F2DA5A78F873564061A38F81BA88 | CONFIRMED | arquivo fornecido, não prova procedência |
| com.interactive.brasiliptv / 5.50.0 / 55000; SDK 19/33/34 | CONFIRMED | aapt local |
| lib/ contém ARM32 e ARM64, sem x86 | CONFIRMED | inventário ZIP |
| kcptun-client e kcptun-client-live têm ELF ARM32 | CONFIRMED | cabeçalhos e_machine=40 |
| libranger-jni e player IJK/FFmpeg presentes | CONFIRMED | presença não prova execução |
| Shell s.h.e.l.l e assets ijiami | HIGH_CONFIDENCE | família de proteção; versão UNKNOWN |
| JNI_OnLoad e imports de sockets no ranger ARM64 | CONFIRMED | ELF com aviso de tabela; não prova gateway |
| Login, sessão, catálogo, dois canais, fonte, primeiro frame | UNKNOWN | baseline anterior ao teste físico; atualização abaixo |

Detalhes: [análise estática](brasil-tv-5.50-static-analysis.md).
A evidência de 5.2 acima não é um contrato da 5.50.

## Atualização 5.50 — gate físico — 2026-10-20

| Evidência | Classificação | Limite |
|---|---|---|
| J7 API 27 ARM32, APK instalado idêntico, Home e gate na UI | CONFIRMED | observação direta ADB |
| phone, UI normal, ausência Leanback/television | CONFIRMED | somente J7 |
| Gate funcional após startup, não recusa de instalação | HIGH_CONFIDENCE | regra interna UNKNOWN |
| Elegibilidade por tipo/modelo, possivelmente TV | LIKELY | falta controle TV e critério do fornecedor |
| Threads ranger/handlerRanger no processo | CONFIRMED | mapa de libs inacessível |
| Componente Ranger ativo | HIGH_CONFIDENCE | JNI/lifecycle completo UNKNOWN |
| SELinux nega read de proc_stat/proc à thread ranger | CONFIRMED | causalidade com gate UNKNOWN |
| Mesmo gate no SM-T295 API 30 ARM32/64 | LIKELY | relato do operador; sem ADB nesta sessão |
| Condição exata, DRM, contratos remotos, playback | UNKNOWN | sem canal ou sessão inspecionada |

Detalhes: [diagnóstico e próximo teste](brasil-tv-5.50-device-gate.md).
