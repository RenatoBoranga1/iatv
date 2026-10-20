# Brasil TV 5.50 — análise estática

Data: 2026-10-20. Inspeção read-only do arquivo fornecido pelo operador.
Baseline Git: afbacb09fa72e3b6b3c0a8a74695e54847459361.
Não houve instalação, login, execução de binário extraído ou alteração do APK.

## Identidade — CONFIRMED

| Campo | Resultado |
|---|---|
| Arquivo | BrasilTV_5.50.apk, preservado fora do repositório |
| Bytes | 34449510 |
| SHA-256 | DD7F35A5949BECE00704BDF7D9D7A7431945F2DA5A78F873564061A38F81BA88 |
| MD5 (identificação, não segurança) | 2B070A7D593916EBEFC8D07705BB2F4A |
| packageName | com.interactive.brasiliptv |
| versionName / versionCode | 5.50.0 / 55000 |
| minSdk / targetSdk / compileSdk | 19 / 33 / 34 |
| Application | s.h.e.l.l.S |
| AppComponentFactory | s.h.e.l.l.A |
| Launcher normal e Leanback | com.interactive.brasiliptv.ui.activity.WelcomeActivity |
| ABIs em lib/ | arm64-v8a, armeabi-v7a |

`git check-ignore research/apks/BrasilTV_5.50.apk` confirmou a regra `*.apk`.
Nenhuma cópia do APK é necessária no repositório.

## Assinatura — CONFIRMED, proveniência UNKNOWN

apksigner 35.0.0: Verifies; v1 e v2 true; v3/v3.1/v4 e SourceStamp false.
Um signatário, RSA. Certificado SHA-256:
c8a2e9bccf597c2fb6dc66bee293fc13f2fc47ec77bc6b2b0d52c11f51192ab8.
DN declarado: EMAILADDRESS=android@android.com, CN=Android, OU=Android,
O=Android, L=Mountain View, ST=California, C=US.
O DN é autodeclarado: não comprova autoria da Google nem autenticidade comercial.
O verificador avisa que entradas META-INF não são protegidas pela assinatura JAR.
Nenhuma entrada foi removida ou assinatura alterada. A procedência oficial exige
comparação com fingerprint publicado pelo fornecedor autorizado.

## Proteção e limites

CONFIRMED: classes.dex de 13628 bytes; assets/ijiami.dat (4240861),
ijiami.ajm (2296175), IJMDal.Data (17760), ijm_lib com libexec/libexecmain.
HIGH_CONFIDENCE: bootstrap/protetor da família Ijiami, pelos nomes e shell declarados.
UNKNOWN: versão exata, classes do payload protegido, fluxo de autenticação,
ordem de carregamento nativo e mecanismo interno. Nenhum payload foi desempacotado.
LIKELY não foi promovido a contrato: nomes de assets e símbolos não provam uso.

## ABI e executáveis

CONFIRMED por ZIP e cabeçalho ELF: libranger-jni em AArch64 (e_machine 183)
e ARM (40). assets/kcptun-client (2391936 bytes) e kcptun-client-live
(2271580 bytes) têm cabeçalhos ELF ARM de 32 bits (40).
O segundo declara tipo ELF DYN; nome de executável não comprova que seja lançado.
O dispositivo candidato deve anunciar suporte a armeabi-v7a, além de satisfazer
minSdk 26 do próprio IA TV. Não insistir no AVD x86.

## Símbolos e strings

llvm-readelf do NDK 28.2 confirmou JNI_OnLoad exportado em libranger-jni ARM64
e imports socket, connect, bind, accept, listen e socketpair.
Aviso do leitor: nchain 732 diverge da contagem 3 derivada de SHT_DYNSYM.
Portanto o inventário de símbolos não é tratado como completo.
Não foi observado um método Java de resolver/playback com contrato recuperável.

Busca ASCII case-insensitive em entradas lib/, assets/ e DEX, somente registrando
presença dos termos da lista autorizada, sem imprimir strings completas ou valores.
Substring pode pertencer a biblioteca genérica ou dados coincidentes.
Ausência nesta busca não prova ausência no código protegido.
Inventário detalhado abaixo registra somente nomes de arquivos, tamanho, ELF e
termos; não contém endpoints extraídos, tokens ou payloads.

## Comparação objetiva com 5.2

| Aspecto | 5.2 documentado | 5.50 revalidado |
|---|---|---|
| Pacote | nd.brasiltvnew.top | com.interactive.brasiliptv |
| Versão/código | 5.2 / 600 | 5.50.0 / 55000 |
| SDK mínimo/alvo | 16 / 29 | 19 / 33 |
| ABIs | ARM32/ARM64/x86/x86_64 | ARM32/ARM64 |
| Launcher | org.sopcast.android.SopCast | WelcomeActivity |
| Application | com.mcal.apkprotector.ProxyApplication | s.h.e.l.l.S |
| Bootstrap DEX | 13984 bytes | 13628 bytes |
| Artefatos nativos | TVCore, gojni, FFmpeg | ranger-jni, ijk, kcptun assets |
| Runtime conhecido | login na API 29 x86 | NÃO EXECUTADO |

Não é upgrade instalável do mesmo packageName. A equivalência dos fornecedores,
contratos, contas, sessão e autorização de redistribuir core é UNKNOWN.
CAuth/TVCore da 5.2 não são automaticamente aplicáveis à 5.50.

## Reprodução da análise

Ferramentas: PowerShell Get-FileHash (SHA256/MD5), ZIP .NET read-only,
aapt 35.0.0 dump badging / dump xmltree AndroidManifest.xml,
apksigner 35.0.0 verify --verbose --print-certs, llvm-readelf --dyn-syms --wide.
Bibliotecas selecionadas foram extraídas apenas em `.tools/`, ignorado pelo Git,
para leitura ELF. Nenhum binário foi executado.
Sem dado de runtime, os contratos continuam REQUIRES_PROVIDER_CONTRACT.

## Inventário ZIP sanitizado — CONFIRMED

| Entrada | Bytes | ELF e_machine | Termos presentes (não contratos) |
|---|---:|---|---|
| assets/dexopt/baseline.prof | 274 |  |  |
| assets/dexopt/baseline.profm | 49 |  |  |
| lib/arm64-v8a/libc++_shared.so | 1027152 | 183 | stream, hls, port |
| lib/arm64-v8a/libcrashlytics-common.so | 774488 | 183 | auth, stream, http, broker, socket, port, jni, ijk |
| lib/arm64-v8a/libcrashlytics-handler.so | 158648 | 183 | stream, http, jni |
| lib/arm64-v8a/libcrashlytics-trampoline.so | 4768 | 183 | http |
| lib/arm64-v8a/libcrashlytics.so | 166896 | 183 | stream, http, jni |
| lib/arm64-v8a/libcrashsdk.so | 647928 | 183 | auth, live, stream, rtmp, http, socket, port, jni |
| lib/arm64-v8a/libijkffmpeg.so | 6445696 | 183 | auth, login, token, session, channel, live, stream, playback, hls, m3u8, mpegts, mpeg-ts, dash, rtmp, http, socket, localhost, port, jni, player, ijk, ffmpeg, ranger |
| lib/arm64-v8a/libijkplayer.so | 317960 | 183 | session, channel, live, stream, playback, rtmp, http, port, jni, player, ijk, ffmpeg, ranger |
| lib/arm64-v8a/libijksdl.so | 380888 | 183 | session, channel, stream, playback, http, port, jni, player, ijk, ffmpeg |
| lib/arm64-v8a/libtnet-3.1.14.so | 362872 | 183 | auth, session, live, stream, http, socket, port, jni, ijk |
| lib/arm64-v8a/libumeng-spy.so | 464184 | 183 | stream, http |
| lib/armeabi-v7a/libboost_multidex.so | 13804 | 40 | jni |
| lib/armeabi-v7a/libc++_shared.so | 610048 | 40 | live, stream, port |
| lib/armeabi-v7a/libcrashlytics-common.so | 456204 | 40 | auth, stream, http, broker, socket, port, jni, ijk |
| lib/armeabi-v7a/libcrashlytics-handler.so | 72324 | 40 | stream, http, port, jni |
| lib/armeabi-v7a/libcrashlytics-trampoline.so | 3356 | 40 | http |
| lib/armeabi-v7a/libcrashlytics.so | 78336 | 40 | stream, http, port, jni |
| lib/armeabi-v7a/libcrashsdk.so | 491764 | 40 | auth, live, stream, rtmp, http, socket, port, jni |
| lib/armeabi-v7a/libijkffmpeg.so | 5182008 | 40 | auth, login, token, session, channel, live, stream, playback, hls, m3u8, mpegts, mpeg-ts, dash, rtmp, http, socket, localhost, port, jni, player, ijk, ffmpeg, ranger |
| lib/armeabi-v7a/libijkplayer.so | 243828 | 40 | session, channel, live, stream, playback, rtmp, http, port, jni, player, ijk, ffmpeg, ranger |
| lib/armeabi-v7a/libijksdl.so | 303808 | 40 | session, channel, stream, playback, http, port, jni, player, ijk, ffmpeg |
| lib/armeabi-v7a/libtnet-3.1.14.so | 250244 | 40 | auth, session, live, stream, http, socket, port, jni, ijk |
| lib/armeabi-v7a/libumeng-spy.so | 265740 | 40 | stream, http, port |
| assets/amazingkids.otf | 194424 |  | http, ijk |
| assets/domain_test.json | 330 |  | port |
| assets/google.wav | 8046 |  | ijk |
| assets/kcptun-client-live | 2271580 | 40 | auth, http, jni, ijk |
| assets/kcptun-client | 2391936 | 40 | auth, token, session, channel, live, stream, dash, http, resolver, socket, localhost, 127.0.0.1, port, ijk, kcptun, ranger |
| assets/test1.html | 314 |  |  |
| assets/therouter/routeMap.json | 1978 |  | login, http |
| lib/arm64-v8a/libranger-jni.so | 8026192 | 183 | auth, token, session, live, stream, hls, mpegts, rtmp, http, resolver, socket, port, jni, ijk, ranger |
| lib/armeabi-v7a/libranger-jni.so | 3468344 | 40 | auth, token, session, live, stream, hls, mpegts, rtmp, http, resolver, socket, port, jni, ijk, ranger |
| classes.dex | 13628 |  | stream, port |
| assets/images/icon_titl_last_encrypted.png | 16 |  |  |
| assets/images/cfg_cjdk_md_fes.png | 0 |  |  |
| assets/images/xxx_Opti_ez_xxexxx.png | 0 |  |  |
| assets/images/cfg_legacy_eddts.png | 0 |  |  |
| assets/ijm_lib/armeabi/libexecmain.so | 17544 | 40 |  |
| assets/ijm_lib/armeabi/libexec.so | 435600 | 40 | live, stream, port, jni |
| assets/ijm_lib/arm64-v8a/libexecmain.so | 18200 | 183 | http |
| assets/ijm_lib/arm64-v8a/libexec.so | 636696 | 183 | stream, hls, http, port, jni, ijk |
| assets/ijiami.dat | 4240861 |  | hls, jni, ijk |
| assets/ijiami.ajm | 2296175 |  | jni, ijk |
| assets/IJMDal.Data | 17760 |  |  |
| assets/sign_verify.png | 48 |  |  |
| assets/icon/image_icon_constructor_encrypted.png | 32 |  |  |
| assets/images/comxx_ca_tk.png | 496 |  |  |
| assets/signed.bin | 65328 |  |  |
| assets/af.bin | 31536 |  |  |

## Componentes declarados — CONFIRMED

| Tipo | Classe |
|---|---|
| activity | com.voice.activity.VoiceSearchActivity |
| activity | com.interactive.brasiliptv.ui.activity.VoiceSearchActivity |
| activity | com.interactive.brasiliptv.ui.activity.WelcomeActivity |
| activity | com.interactive.brasiliptv.ui.activity.GuidePageActivity |
| service | com.interactive.brasiliptv.service.MyFirebaseMessagingService |
| activity | com.main.ui.activity.HomeActivity |
| activity | com.vod.ui.activity.VodDetailsActivity |
| activity | com.vod.ui.activity.VodCategoryActivity |
| activity | com.vod.ui.activity.VodSearchActivity |
| activity | com.vod.ui.activity.RestrictLevelSearchActivity |
| activity | com.vod.ui.activity.TopicActivity |
| activity | com.vod.ui.activity.TopicMoreActivity |
| activity | com.vod.ui.activity.ActorDetailsActivity |
| activity | com.vod.ui.activity.KidsCategoryActivity |
| activity | com.vod.ui.activity.FilterActivity |
| activity | com.vod.ui.activity.SmartvListActivity |
| activity | com.live.ui.activity.LiveFreeActivity |
| activity | com.live.ui.activity.LiveVoiceSearchActivity |
| activity | com.live.ui.activity.LiveNewVoiceSearchActivity |
| activity | com.mine.ui.activity.CouponActivity |
| activity | com.mine.ui.activity.UserCenterActivity |
| activity | com.mine.ui.activity.AccountSecurityActivity |
| activity | com.mine.ui.activity.MyRecordActivity |
| activity | com.mine.ui.activity.PurchaseActivity |
| activity | com.mine.ui.activity.PersonalMoreActivity |
| activity | com.mine.ui.activity.OrderHistoryActivity |
| activity | com.mine.ui.activity.DisplayQRCodeActivity |
| activity | com.mine.ui.activity.EventCenterActivity |
| activity | com.mine.ui.activity.InviteFriendsActivity |
| activity | com.mine.ui.activity.SettingsActivity |
| activity | com.login.ui.activity.ForcePasswordChangeActivity |
| activity | com.match.ui.activity.MatchScheduleActivity |
| activity | com.match.ui.activity.MatchDetailActivity |
| activity | com.match.ui.activity.MatchCategoryActivity |
| activity | com.match.ui.activity.MatchRankCategoryActivity |
| activity | com.module.ui.activity.CommonWebActivity |
| activity | com.module.ui.activity.WebActivity |
| service | com.module.receiver.ReportService |
| provider | com.mobile.provider.DownloadFileProvider |
| receiver | com.qiniu.android.dns.NetworkReceiver |
| provider | com.therouter.InnerTheRouterContentProvider |
| service | com.google.firebase.components.ComponentDiscoveryService |
| receiver | com.google.android.gms.measurement.AppMeasurementReceiver |
| service | com.google.android.gms.measurement.AppMeasurementService |
| service | com.google.android.gms.measurement.AppMeasurementJobService |
| receiver | com.google.firebase.iid.FirebaseInstanceIdReceiver |
| service | com.google.firebase.messaging.FirebaseMessagingService |
| activity | com.google.android.gms.common.api.GoogleApiActivity |
| service | com.taobao.accs.ChannelService |
| service | com.taobao.accs.data.MsgDistributeService |
| receiver | com.taobao.accs.EventReceiver |
| receiver | com.taobao.accs.ServiceReceiver |
| service | org.android.agoo.accs.AgooService |
| service | com.umeng.message.UmengIntentService |
| service | com.umeng.message.XiaomiIntentService |
| receiver | com.taobao.agoo.AgooCommondReceiver |
| service | com.umeng.message.UmengMessageIntentReceiverService |
| service | androidx.room.MultiInstanceInvalidationService |
| service | com.google.android.datatransport.runtime.backends.TransportBackendDiscovery |
| service | com.google.android.datatransport.runtime.scheduling.jobscheduling.JobInfoSchedulerService |
| receiver | com.google.android.datatransport.runtime.scheduling.jobscheduling.AlarmManagerSchedulerBroadcastReceiver |
| provider | androidx.startup.InitializationProvider |
| receiver | androidx.profileinstaller.ProfileInstallReceiver |
| service | com.bytedance.boost_multidex.OptimizeService |
| service | com.umeng.message.component.UmengIntentService |
| service | com.umeng.message.component.UmengMessageReceiverService |
| service | com.umeng.message.component.UmengMessageHandlerService |
| provider | com.umeng.message.component.UmengMessageProvider |
| receiver | com.umeng.message.component.UmengNotificationReceiver |
| activity | com.umeng.message.component.UmengNotificationClickActivity |
| activity | com.umeng.message.notify.UPushMessageNotifyActivity |
| activity-alias | com.umeng.message.UMessageNotifyActivity |
| service | com.taobao.accs.ChannelService$KernelService |
| service | com.taobao.accs.internal.AccsJobService |

## Permissões e features — CONFIRMED

- android.permission.MANAGE_EXTERNAL_STORAGE
- com.google.android.gms.permission.AD_ID
- com.google.android.providers.gsf.permission.READ_GSERVICES
- android.permission.INTERNET
- android.permission.READ_EXTERNAL_STORAGE
- android.permission.WAKE_LOCK
- android.permission.WRITE_EXTERNAL_STORAGE
- android.permission.READ_MEDIA_AUDIO
- android.permission.ACCESS_NETWORK_STATE
- android.permission.ACCESS_WIFI_STATE
- android.permission.REQUEST_INSTALL_PACKAGES
- com.google.android.c2dm.permission.RECEIVE
- com.google.android.finsky.permission.BIND_GET_INSTALL_REFERRER_SERVICE
- android.permission.POST_NOTIFICATIONS
- android.permission.MOUNT_UNMOUNT_FILESYSTEMS
- com.huawei.android.launcher.permission.CHANGE_BADGE
- com.vivo.notification.permission.BADGE_ICON

Leanback e touchscreen declarados opcionais; landscape e Wi-Fi são features implícitas reportadas por aapt. Banner declarado res/wN.png. Permissão declarada não significa permissão concedida.
