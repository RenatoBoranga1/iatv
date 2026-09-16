# Android TV

Abra `apps/android-tv` no Android Studio. Use JDK 21, SDK 35 e um emulador Android TV ou Google TV. `minSdk=26`.

```powershell
cd apps/android-tv
.\gradlew.bat assembleDebug testDebugUnitTest lintDebug
.\gradlew.bat assembleDebug -PIATV_API_URL=http://192.168.1.100:3000/
```

A API precisa estar acessível pela TV. `10.0.2.2` aponta para o computador no emulador oficial. Na TV física use o IP local do computador tanto para `IATV_API_URL` quanto para `DEMO_MEDIA_URL`.

Instalação: `adb install -r app/build/outputs/apk/debug/app-debug.apk`. Iniciar: `adb shell am start -n com.iatv.app/.MainActivity`.

Setas movem o foco, OK abre/aciona, Back retorna. Os cards usam borda verde espessa ao focar; controles usam Compose for TV. Lists são lazy e guardam scroll ao abrir detalhes. Busca e IA permitem teclado do sistema; o assistente também oferece perguntas acionáveis pelo D-Pad. Não há voz ainda.

Player Media3 possui controles nativos de play/pause e seek, com HLS/DASH incluídos como capacidade de biblioteca. O único fixture validável nesta fase é MP4. Seleção de faixas depende da mídia; o fixture possui somente uma faixa de áudio. Não há seletor próprio de qualidade nem zapping de canais nesta versão.

Verificação manual necessária: abrir Home; percorrer menu; alcançar cards fora da tela; entrar/sair de detalhes; favoritar; buscar; consultar IA; iniciar demo; play/pause; Back; repetir sem rede; testar a retomada. Validação em controle remoto físico permanece necessária antes de distribuição.
