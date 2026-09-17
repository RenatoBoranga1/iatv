# ADR 001 — Player, recuperação e progresso

Status: aceito, Milestone 2.

Preservar Media3 1.6.1 e PlayerView para controles de TV. Um PlaybackController por
conteúdo, usando applicationContext, cria no START e libera no STOP/dispose. PAUSE
salva progresso; não existe serviço de background. O estado Playing/Paused deriva do
player. O desejo de reprodução e posição sobrevivem ao ciclo stop/start.

Estados explícitos Idle, Preparing, Buffering, Playing, Paused, Ended, Recovering e Error.
Recuperar somente erros transitórios de rede/timeout com 2, 4 e 8 segundos. Orçamento
de três tentativas por sessão, sem reset automático ao receber um frame ou alternar rede.
Retry explícito do usuário renova o orçamento. Timeout de buffering: 30 segundos.
Media3 não tem uma segunda política de tentativas escondida (minimum retry count 0).
Decoder/formato/source exigem ação do usuário. Um manifesto live controla sua própria
janela. Behind-live-window e 404 de segmento live podem renovar o manifesto dentro do mesmo
orçamento de três tentativas; HTTP 429/5xx também são transitórios. 404 VOD é erro de fonte.
Um manifesto live controla sua própria
janela; somente o MP4 fictício legado repete como demonstração.

API: connect 10s, read 20s, write 15s, call 30s. Streaming: connect 10s/read 15s.
Não repetir POST automaticamente. NetworkMonitor usa callbacks removidos ao cancelar;
rede Limited pode alcançar API local, portanto não bloquear HTTP com base em validação
de internet. Após reconexão repetir Home/GET interrompido, nunca consulta IA automaticamente.

Progresso VOD local: positionMs, durationMs, percentage, updatedAt e completed.
Salvar a cada 15 segundos, pausa, seek, stop, final e saída; ignorar duração desconhecida
e gravações idênticas. Conclusão em >=95% ou ENDED. Concluídos saem de Continuar
assistindo; ordem decrescente por updatedAt. Ler a posição legada até a primeira gravação
do novo formato. Não salvar progresso live. Sem requisições de progresso ao backend:
contas/perfis/sincronização ainda não existem, não simular consistência entre aparelhos.

Métricas locais debug: requested, first frame/latência monotônica, buffering duração e
quantidade, erros categorizados, criação/liberação. Nunca registrar texto IA, URLs,
credenciais, IDs invasivos ou stack trace fornecido ao usuário.

Referência: https://developer.android.com/media/implement/playback-app
