# JNI / native inventory
Evidência: strings de símbolos nas bibliotecas x86 do APK com hash no relatório.
Inputs/outputs abaixo são UNKNOWN quando a assinatura Java não foi recuperada.
Nomes sugestivos não provam tipos, retorno ou comportamento.

| Java/JNI symbol | Native library | Input | Output | Probable purpose | Evidence | Confidence |
|---|---|---|---|---|---|---|
| com.tvbus.engine.TVCore.setAuthUrl/setUsername/setPassword | libtvcore.so | UNKNOWN | UNKNOWN | configurar auth | Java_com_tvbus_engine_TVCore_* | CONFIRMED existência |
| TVCore.setMKBroker | libtvcore.so | UNKNOWN | UNKNOWN | configurar broker | símbolo JNI | CONFIRMED existência |
| TVCore.setUrl | libtvcore.so | UNKNOWN | UNKNOWN | recurso de playback | símbolo JNI | CONFIRMED existência |
| TVCore.setPlayPort/getPlayPort | libtvcore.so | UNKNOWN | UNKNOWN | porta de saída | símbolo JNI | CONFIRMED existência |
| TVCore.setSockPath/getSockPath | libtvcore.so | UNKNOWN | UNKNOWN | socket | símbolo JNI | CONFIRMED existência |
| TVCore.initialise/init/start/start2/run/stop/stop2/quit | libtvcore.so | UNKNOWN | UNKNOWN | lifecycle | símbolos JNI | CONFIRMED existência |
| TVCore.diagnose/setListener/getVersion | libtvcore.so | UNKNOWN | UNKNOWN | diagnóstico/callback | símbolos JNI | CONFIRMED existência |
| io.binstream.libtvcar.Libtvcar.setAuthURL/setUsername/setPassword | libgojni.so | UNKNOWN | UNKNOWN | auth | símbolos JNI | CONFIRMED existência |
| Libtvcar.setPlayPort/start/run/release | libgojni.so | UNKNOWN | UNKNOWN | playback/lifecycle | símbolos JNI | CONFIRMED existência |
| Libtvcar proxyListener onInfo/onInited/onPrepared/onStart/onStop/onQuit | libgojni.so | UNKNOWN | UNKNOWN | callback | símbolos JNI | CONFIRMED existência |
| CAuth::SetAuthUrl/SetUsername/SetPassword/SetCurVersion | libtvcore.so | const char* pelo mangling | UNKNOWN | auth | _ZN5CAuth…EPKc | CONFIRMED existência |
| CAuth::ComposePostMessage/parseResponse/GetAuthItems | libtvcore.so | dois int pelo mangling | UNKNOWN | serialização/auth | …Eii | CONFIRMED existência |
| broker_curl_compose_request_message/perform_request/process_response | libtvcore.so | ponteiros nativos | UNKNOWN | broker | símbolos C++ | CONFIRMED existência |
| jni_device_brand/model/id/os_version | libtvcore.so | UNKNOWN | UNKNOWN | identidade | nomes encontrados | CONFIRMED existência |

