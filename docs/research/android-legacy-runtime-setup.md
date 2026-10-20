# Ambiente de pesquisa Android 10 / API 29

Imagem oficial Android TV x86 revisão 3, consultada no índice Google:
https://dl.google.com/android/repository/sys-img/android-tv/sys-img2-3.xml
Arquivo: x86-29_r03.zip, 590809630 bytes.
SHA-1 conferido contra o índice: 736c2f6c79493b5ee4ca0d2531a25c9eb7f9c7ab.
Extração local em `.tools/tv29/x86`, ignorada pelo Git.
O ZIP foi removido depois da extração/verificação para liberar espaço.

Não foi necessário instalar Command-Line Tools nem modificar o SDK existente:
o emulador instalado aceita um AVD com image.sysdir.1 apontando à imagem extraída.
Configuração inicial: x86, Android TV, 1280×720, 1536 MB RAM, 2 CPUs,
GPU swiftshader_indirect, sem áudio e sem snapshots.

Em outubro, o emulador 36.6.11 exigiu 7372,80 MB livres para criar userdata
e reescreveu a partição para 6 GiB. `-partition-size 2048` foi rejeitado pelo
limite anunciado de 2047 MB e não foi adotado. C: não tinha espaço suficiente.
Solução: dados descartáveis no diretório `D:\IATVResearch\provider29.avd`;
descriptor local `.tools/avd/provider29.ini` aponta a esse diretório.
O AVD principal IA TV não foi modificado. Não alterar dispositivos físicos conectados.

```powershell
$env:ANDROID_AVD_HOME = '<repo>\.tools\avd'
Start-Process '<SDK>\emulator\emulator.exe' -WindowStyle Hidden -ArgumentList @(
  '-avd', 'provider29', '-port', '5558', '-no-window', '-no-audio',
  '-no-snapshot', '-gpu', 'swiftshader_indirect'
)
& '<SDK>\platform-tools\adb.exe' -s emulator-5558 shell getprop sys.boot_completed
& '<SDK>\platform-tools\adb.exe' -s emulator-5558 shell getprop ro.build.version.sdk
& '<SDK>\platform-tools\adb.exe' -s emulator-5558 shell getprop ro.product.cpu.abi
& '<SDK>\platform-tools\adb.exe' -s emulator-5558 install --no-streaming '<APK original>'
& '<SDK>\platform-tools\adb.exe' -s emulator-5558 shell am start -W -n nd.brasiltvnew.top/org.sopcast.android.SopCast
```

Conferir serial em todos os comandos. Se offline após boot, `adb reconnect offline`
foi suficiente neste ambiente. Não salvar logcat bruto: usar o coletor sanitizado
`scripts/collect-legacy-crash.ps1`. UI XML foi lido em memória e filtrado antes da
saída; nenhum screenshot contendo dados de login/device foi versionado.
