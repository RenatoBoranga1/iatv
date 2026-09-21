# Brasil TV 5.2 — análise estática
## APK metadata
Original preservado em Downloads: Brasil_TV_New_VIP-5.2.apk.
SHA-256: A2E141DB8CFD50AFF7EB1034CE60A7D151BF5522007EAF0FDB7975A5A7AD3682.
aapt 35.0.0: pacote nd.brasiltvnew.top, versionName 5.2, versionCode 600,
minSdk 16, targetSdk 29; launcher org.sopcast.android.SopCast.
ABIs: armeabi-v7a, arm64-v8a, x86, x86_64.
## Protection / DEX
CONFIRMED: classes.dex 13.984 bytes e assets/dex/classes-v1.bin 1.972.372 bytes.
HIGH_CONFIDENCE: bootstrap protegido/carregamento posterior; mecanismo não reconstruído.
## Native libraries / JNI / C++ symbols
CONFIRMED por entradas ZIP e strings restritas a símbolos nas bibliotecas x86:
libtvcore.so (4.359.960 bytes), libgojni.so (9.361.543 bytes),
libffmpeg.so, libavcodec.so, libavutil.so, libswresample.so e libBugly.so.
Ver inventário JNI. A presença do símbolo não prova que foi chamado.
## Auth
CONFIRMED: CAuth SetAuthUrl/SetUsername/SetPassword/SetCurVersion,
ComposePostMessage/performPost/parseResponse/GetAuthItems.
UNKNOWN: método HTTP real, campos, endpoint e resposta; nome performPost não substitui captura.
## Device
CONFIRMED: jni_device_brand/model/id/os_version em libtvcore.
UNKNOWN: origem e persistência do ID e participação efetiva no request.
## Broker
CONFIRMED: broker_curl_compose_request_message/perform_request/process_response
e speer_msg_get_stream_peerslist_broker/set_stream_originator/get_stream_header.
LIKELY: resolução distribuída; ordem e payload UNKNOWN.
## Live / VOD / EPG
Contratos UNKNOWN. Nenhuma URL ou credencial extraída foi usada como contrato.
## Playback / Protocols / Player
TVCore setUrl/setPlayPort/getPlayPort/start/run/stop confirmados estaticamente.
setSockPath/getSockPath também presentes. Porta ou socket não provam gateway HTTP.
Referências sop/tvbus/tvcar/HLS/MPEG-TS foram informadas no escopo anterior;
não determinam o formato final desta sessão. Player final UNKNOWN.
## Método e limites
Inspeção read-only do ZIP, aapt badging, filtro de strings para nomes de símbolos.
Nenhum dump bruto, APK ou biblioteca nativa será versionado.
Não houve descompilação do payload protegido nem quebra de criptografia.

