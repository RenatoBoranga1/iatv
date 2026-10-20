# Brasil TV 5.2 — startup crash investigation

Atualizado em 2026-10-20. APK original SHA-256
`A2E141DB8CFD50AFF7EB1034CE60A7D151BF5522007EAF0FDB7975A5A7AD3682`.
Pacote `nd.brasiltvnew.top`, versão 5.2/code 600. Nenhum patch aplicado.

## Evidência API 34

CONFIRMED: instalação limpa e limpeza de dados reproduziram a mesma exceção
na sessão de pesquisa anterior, no AVD API 34 x86. Os frames sanitizados foram
preservados localmente e relidos nesta retomada. Não são uma nova execução em outubro.

```text
java.lang.ClassCastException: java.lang.Long cannot be cast to java.lang.Integer
at cc.binmt.signature.PmsHookApplication.invoke(PmsHookApplication.java:37)
at java.lang.reflect.Proxy.invoke(Proxy.java:1006)
at $Proxy2.getPackageInfo(Unknown Source)
at android.content.pm.PackageManager.getPackageInfoAsUserUncached(PackageManager.java:10903)
at android.content.pm.PackageManager$2.recompute(PackageManager.java:10916)
at android.content.pm.PackageManager$2.recompute(PackageManager.java:10913)
at android.app.PropertyInvalidatedCache.query(PropertyInvalidatedCache.java:999)
at android.content.pm.PackageManager.getPackageInfoAsUserCached(PackageManager.java:10930)
at android.app.ApplicationPackageManager.getPackageInfoAsUser(ApplicationPackageManager.java:251)
at android.app.ApplicationPackageManager.getPackageInfo(ApplicationPackageManager.java:216)
at android.app.ApplicationPackageManager.getPackageInfo(ApplicationPackageManager.java:210)
at c.c.a.c.<init>(MyApplication:18)
at org.sopcast.android.SopCast.onCreate(MyApplication:18)
at android.app.Activity.performCreate(Activity.java:8595)
at android.app.Activity.performCreate(Activity.java:8573)
at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1456)
at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:3764)
```

Frames do wrapper de lançamento, também observados:

```text
at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:3782)
at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:3922)
at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:103)
at android.app.servertransaction.TransactionExecutor.executeCallbacks(TransactionExecutor.java:139)
at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:96)
at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2443)
at android.os.Handler.dispatchMessage(Handler.java:106)
at android.os.Looper.loopOnce(Looper.java:205)
at android.os.Looper.loop(Looper.java:294)
at android.app.ActivityThread.main(ActivityThread.java:8177)
at java.lang.reflect.Method.invoke(Native Method)
at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:552)
at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:971)
```

Os frames são a saída sanitizada/deduplicada do coletor, não um logcat bruto.
`MyApplication:18` é informação de debug do APK, não uma linha de fonte recuperada.

## Origem do Long e contexto do cast

CONFIRMED: a exceção aparece no invocation handler `PmsHookApplication.invoke`,
no caminho de consulta de informações do pacote. Não aparece em `getInt`/`getLong`.
HIGH_CONFIDENCE: o hook espera flags `Integer` da interface antiga e recebe
flags `Long` da interface moderna. AOSP confirma `int flags` no Android 10 e
`long flags` no Android 13. Isso explica por que o mesmo APK inicia na API 29.

Fontes primárias:
- [IPackageManager Android 10](https://android.googlesource.com/platform/frameworks/base/+/refs/heads/android10-release/core/java/android/content/pm/IPackageManager.aidl)
- [IPackageManager Android 13](https://android.googlesource.com/platform/frameworks/base/+/refs/heads/android13-release/core/java/android/content/pm/IPackageManager.aidl)

UNKNOWN: instrução exata e índice do argumento dentro do hook. O corpo não foi
recuperado no pequeno DEX bootstrap inspecionado com dexdump. Não foi necessário
desproteger ou modificar o APK para o teste comparativo.

## Persistência e hipóteses

| Teste | Evidência / resultado |
|---|---|
| API 34 instalação limpa → primeiro launch | CRASH_SAME; trace preservado |
| API 34 limpar dados → launch | CRASH_SAME; trace preservado |
| API 34 force-stop / reinstalação posterior | instalação confirmou Success, mas captura final sem trace conclusivo; NÃO contar como PASS |
| API 29 instalação limpa → primeiro launch | login alcançado sem o crash de cast |

Persistência antiga é uma explicação pouco sustentada: o problema sobrevive à
remoção dos dados, e o stack aponta para PackageManager. Isso não prova ausência
de preferências no restante do startup. Bundle/Intent/JSON/SQLite/cache remoto,
retorno JNI e propriedades do dispositivo permanecem UNKNOWN como fontes de outros
dados; não há evidência que os ligue a este cast. Proxy de PackageManager é a
hipótese de alta confiança para este erro específico.

## Manifest e ABI

CONFIRMED via aapt: minSdk 16, targetSdk 29; application
`com.mcal.apkprotector.ProxyApplication`; launcher `org.sopcast.android.SopCast`;
debuggable=false. Service `com.tvbus.engine.TVService`, receiver
`org.sopcast.android.broadcast.ActionReceiver`, FileProvider não exportado e
activity `com.mcal.apkprotector.activities.CopyClipActivity`.
Features incluem leanback e touchscreen. Permissões declaradas incluem Internet,
rede/Wi-Fi, estado de telefone, armazenamento externo, boot, wake lock, áudio,
overlay, instalação de pacotes e permissões legadas de owner/mount.
Declaração não prova concessão ou uso. Manifest não foi alterado.

| Componente | ABI |
|---|---|
| APK / libtvcore / libgojni / libffmpeg | armeabi-v7a, arm64-v8a, x86, x86_64 |
| AVD API 34 | x86 |
| AVD API 29, confirmado por getprop | x86 |

Não há evidência de falha de ABI neste crash Java. A existência de APIs internas
interceptadas torna targetSdk 29 insuficiente para garantir execução em API 34.

## Sequência e limite da conclusão

CONFIRMED pelos frames: SopCast.onCreate → c.c.a.c → PackageManager.getPackageInfo
→ proxy → PmsHookApplication.invoke → ClassCastException na API 34.
Application declarada: CONFIRMED pelo manifest; ordem de preferências, device,
config, rede e carregamento nativo antes do cast: UNKNOWN.
Não afirmar que TVCore não carregou apenas porque não aparece no stack Java.

Decisão: com UI alcançada na API 29, parar investigação/correção da API 34.
