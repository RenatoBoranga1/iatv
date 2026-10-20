# Brasil TV 5.50 — diagnóstico observacional do SM-G610M

Data: 2026-10-20. Estado: BLOCKED_BY_COMPATIBILITY_GATE.
Repositório: C:/Users/rebor/Playground/iatv.
Branch: milestone-2.5-provider-integration.
Base: 785725c084708da5d8da44f65b79d8db358c5685.

## Conclusão

LIKELY: política de elegibilidade por tipo/modelo de aparelho, possivelmente TV.
J7 anuncia phone, UI normal e não anuncia Leanback/television; telefone e tablet
chegam à Home e recusam Lista de Canais. A correlação não identifica o predicado.
UNKNOWN: causa exata, regra local/remota, allowlist de modelos, integridade,
certificação, DRM, codec ou configuração de serviço. Não afirmar TV-only como fato.
HIGH_CONFIDENCE: gate funcional depois de startup, não recusa de instalação por
ABI/minSdk. Processo permanece ativo e a mensagem está na UI em HomeActivity.

## Evidências diretas — CONFIRMED

- APK local: 34449510 bytes. SHA-256 local e do base.apk instalado iguais:
  DD7F35A5949BECE00704BDF7D9D7A7431945F2DA5A78F873564061A38F81BA88.
- Pacote com.interactive.brasiliptv, versão 5.50.0/55000, minSdk 19/targetSdk 33.
- SM-G610M: Android 8.1.0/API 27; abilist e abilist32 armeabi-v7a,armeabi;
  abilist64 vazio; primaryCpuAbi armeabi-v7a; secondaryCpuAbi null.
- ro.build.characteristics=phone; ro.hardware=samsungexynos7870; ro.kernel.qemu=0.
- pm list features: touchscreen, landscape e Wi-Fi presentes; ausência de
  android.software.leanback e android.hardware.type.television.
- dumpsys uimode: mCurUiMode=0x11, tipo 0x1 NORMAL.
- Display integrado 1080x1920, aproximadamente 60 Hz, density 480;
  FLAG_SECURE e FLAG_SUPPORTS_PROTECTED_BUFFERS não comprovam DRM.
- aapt revalidou ARM32/ARM64, Leanback/touchscreen opcionais e landscape/Wi-Fi
  requeridos. Manifest opcional não exclui restrição adicional em runtime.
- Operador abriu Lista de Canais. UI confirmou exatamente:
  “Seu dispositivo não é compatível com este aplicativo.”
- dumpsys activity/window: HomeActivity ativa; processo vivo durante o gate.
- UI obtida via exec-out uiautomator dump /proc/self/fd/1 em memória.
  Tentativa anterior via /dev/tty não entregou XML e não é evidência negativa.
- ps -T e Tgid em /proc/<tid>/status confirmaram threads ranger e handlerRanger
  dentro do processo do aplicativo. HIGH_CONFIDENCE: componente Ranger ativo;
  JNI_OnLoad específico e lifecycle completo continuam UNKNOWN.
- Log audit: SELinux denied read por comm=ranger em stat (proc_stat), diskstats
  e loadavg (proc), permissive=0. TID atual correlacionado ao processo do app.
  getenforce=Enforcing. Relação dessas recusas com o gate: UNKNOWN.
- /proc/<pid>/maps: Permission denied; leitura não elevada. Mapa completo das
  libs, carregamento IJK/FFmpeg e execução de kcptun continuam UNKNOWN.
- Snapshot ps -A sem processo nomeado kcptun; não exclui execução efêmera ou
  outro nome. Não houve seleção de canal ou reprodução.
- Buffer logcat finito do PID sem UnsatisfiedLinkError, dlopen failed,
  cannot locate symbol, wrong ELF class, Exec format error, FATAL EXCEPTION ou
  SIGSEGV. Buffer crash sem nome do pacote. Não prova ausência em toda execução.
- Unsupported class loader e Skipping duplicate class check due to unsupported
  classloader são avisos observados; não identificam a regra do gate.
  Logs Ranger estavam no audit: filtro somente pelo PID do app os omitiria.

## Codecs e DRM

CONFIRMED: configuração XML declara decoders Exynos MPEG4, H263, AVC, AVC secure,
VP8 e HEVC; inclui configurações Google de vídeo/áudio/telefonia. Parse XML
excluiu exemplos em comentários. Primeira tentativa falhou por BOM, repetição
removendo o BOM passou. Inventário não é teste de decodificação.
CONFIRMED: processos mediadrmserver/media.codec e serviços media.drm e
drm.drmManager presentes. dumpsys media.drm retornou vazio.
UNKNOWN: nível Widevine, provisionamento, certificação, exigência de DRM do app.
Não inferir ausência de DRM nem L1/L3 de um dump vazio.

## Comparação com SM-T295

Só o J7 estava conectado. Dados do tablet são relato do operador, sem ADB nesta
sessão: Android 11/API 30, arm64-v8a e armeabi-v7a, instala, inicia, Home aparece,
não exige login e mostra o mesmo gate ao abrir Lista de Canais. Reprodução no
tablet: LIKELY com base no relato; properties, UI mode, features, threads, DRM e
codecs do tablet: UNKNOWN. Não atribuir medições do J7 ao tablet.
No J7, ausência de login foi relatada pelo operador; sessão interna UNKNOWN.
O gate nos dois não sustenta que mudar apenas ARM32 para ARM64 resolveria.

## Próximo teste

Necessário testar Android TV/Fire TV real com o mesmo APK original, suporte
ARM32 confirmado e ADB. Isso testa a hipótese; não garante compatibilidade ou
justifica compra. Comparar hash, API/ABI, features, UI mode, Home e a mesma ação
Lista de Canais. Sucesso reforça hipótese TV, mas modelo/API/serviço também variam;
falha enfraquece hipótese exclusivamente phone/tablet. Consultar critérios oficiais
do fornecedor e coletar o mesmo inventário do SM-T295 quando disponível.
Sem patch, root, hooks, spoofing, alteração de assinatura, bypass ou canais.

## Preservação, testes e entrega

Leituras: getprop restrito; pm features/path; dumpsys package/activity/window/
display/uimode; ps; logcat -d finito, com/sem filtro PID; serviços DRM; XML de
codecs; hash instalado; UI por stdout. Sem limpeza de logcat/dados ou reinstalação.
Sem APK, libs, kcptun, dumps brutos, tokens, cookies, sessões ou identificadores
pessoais no relatório. BrasilTvProvider e mock inalterados, sem contrato novo.

Os quatro comandos Git iniciais confirmaram a branch e o SHA base, com working
tree limpo, usando safe.directory somente por comando. A escrita foi inicialmente
negada por NTFS; o operador concedeu Modificar antes da integração documental.
Somente documentação foi alterada. Builds/testes de aplicação não reexecutados;
as aprovações anteriores permanecem históricas. Validação desta entrega:
revisão do diff, links relativos, escopo de arquivos e ausência de artefatos
proibidos/segredos nos textos adicionados. Nenhum merge em main.

Interpretação de UI mode:
[Android Configuration](https://developer.android.com/reference/android/content/res/Configuration).
