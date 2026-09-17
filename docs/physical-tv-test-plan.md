# Plano de TV física

Status: REQUIRES_PHYSICAL_DEVICE. Preencher [resultados](physical-tv-test-results.md).
Usar pelo menos uma Android TV/Google TV e uma box modesta, rede controlada e mídia própria.

1. Instalar APK, abrir, reiniciar aparelho e atualizar APK com `-r`; confirmar favoritos/progresso.
2. Executar toda a [matriz D-Pad](tv-focus-test-matrix.md), 20 minutos de navegação, sem toque.
3. MP4/HLS/DASH VOD: play, pause, seek, fim, voltar, retomada, troca de conteúdo.
4. HLS live próprio: borda live, EPG fictício identificado, controles, retorno ao canal.
5. Desligar Wi-Fi por 10s e 60s, religar; testar Wi-Fi conectado sem internet e API local.
   Confirmar buffering, tentativas 2/4/8s, limite três e Retry/Back. Não capturar tráfego alheio.
6. Android Home, retorno, standby/retorno, encerramento pelo sistema e recriação. Confirmar
   ausência de áudio em background e pares player_created/player_released nos logs debug.
7. Executar live próprio continuamente por 2h; capturar memória em 0/15/30/60/90/120 min.
   Registrar PSS/Java/native/graphics, crashes, freezes, buffering e temperatura percebida.
8. Repetir entrada/saída do player 30 vezes; PSS deve estabilizar após aquecimento/GC,
   sem crescimento monotônico associado a novas instâncias. Comparar mesmo conteúdo/resolução.

Critério: nenhum crash/ANR, UI responsiva, foco visível, progresso consistente, uma instância
ativa por vez. Falha bloqueia aprovação física. Duas horas não podem ser inferidas de smoke test.
Comandos de coleta em [ADB](adb-tv-testing.md). Guardar evidência sem identificadores pessoais.
