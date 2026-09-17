# ADB — IA TV

Ativar depuração apenas no aparelho de teste e autorizar o computador. Selecionar o
serial retornado por `adb devices`; substituir SERIAL abaixo. Não executar limpeza
de dados ou desinstalação durante teste de preservação de progresso.

```powershell
adb devices -l
adb -s SERIAL install -r apps/android-tv/app/build/outputs/apk/debug/app-debug.apk
adb -s SERIAL shell am start -n com.iatv.app/.MainActivity
adb -s SERIAL shell input keyevent 19
adb -s SERIAL shell input keyevent 20
adb -s SERIAL shell input keyevent 21
adb -s SERIAL shell input keyevent 22
adb -s SERIAL shell input keyevent 23
adb -s SERIAL shell input keyevent 4
adb -s SERIAL logcat -v threadtime IaTvTelemetry:D AndroidRuntime:E '*:S'
adb -s SERIAL shell dumpsys meminfo com.iatv.app
adb -s SERIAL shell dumpsys activity activities
```

19/20/21/22 = cima/baixo/esquerda/direita; 23 = OK; 4 = Back. Keyevent 3 envia Home.
Para registrar uma sessão, redirecionar logs a arquivo local ignorado pelo Git, interromper
com Ctrl+C após 2h. Coletar meminfo em intervalos do plano físico. Logs nativos do sistema
podem conter URLs; revisar/redigir antes de compartilhar. IA TV não registra consultas.

Somente para reset intencional de dados de teste (apaga favoritos/progresso/cache):
```powershell
adb -s SERIAL shell pm clear com.iatv.app
```
Somente para remover o aplicativo de teste:
```powershell
adb -s SERIAL uninstall com.iatv.app
```

APK debug permite HTTP na LAN. Release exige HTTPS. Não desabilitar verificação TLS.
Desativar depuração após concluir o teste em dispositivo físico.
