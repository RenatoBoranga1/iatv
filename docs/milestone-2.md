# Milestone 2 — hardening

Estado: hardening implementado; aprovação em dispositivo físico pendente.
Base auditada em [baseline](milestone-2-baseline.md); branch `milestone-2-hardening`.

## Mudanças

- Estado salvo de detalhe/player, carrosséis e foco; atualização de favoritos/progresso no retorno.
- Estados de tela Loading/Content/Empty/Offline/Error e erro de API separado de rede.
- NetworkMonitor, timeouts HTTP explícitos, recuperação de GET após reconexão.
- Controller Media3 com estados explícitos, liberação em STOP, retry limitado, buffering e primeiro frame.
- Controles PlayerView, seleção de áudio/legenda/qualidade, EPG e Retry/Back.
- WatchProgress local com duração, percentual, data, conclusão e ordenação; migração de posição legada.
- Telemetry/CrashReporter debug sem consultas/URLs, diagnóstico e informações de build/dispositivo.
- Erros API padronizados, request ID seguro, IDs/data validados, health e rate limit IA 30/min.
- Nest/Swagger atualizados dentro da versão principal; teste e lint preservados.
- Admin com loading/error/global-error, sem expor operações administrativas inexistentes.
- Fixtures próprias MP4/HLS/DASH e gerador live; documentação de teste físico e mobile.

## Performance e imagens

Listas continuam lazy com chaves estáveis. Instâncias de Gson e cliente HTTP reutilizadas;
serialização do cache Home fora da main thread. Um player por tela; jobs/listeners cancelados
ao sair. Busca de item no banco usa findUnique em vez de carregar catálogo inteiro.

O seed possui oito itens. Paginação de catálogo não foi adicionada artificialmente; antes
de ampliar o catálogo, substituir all() por cursor estável e pages limitadas, sem quebrar /v1.
Carrosséis não carregam bitmaps remotos: gradiente/título são placeholder/fallback imediato.
Não existe imagem 4K sendo decodificada e não foi adicionada biblioteca de cache ociosa.

Estratégia para primeiro provider com imagens: URLs variantes por poster/backdrop/logo/thumbnail;
dimensionar decode ao tamanho real em pixels do card, máximo 512px poster/thumbnail, 256px logo,
1280px backdrop; carregar somente um backdrop selecionado. Cache LRU limitado à memória do
aparelho e disco com teto; placeholder atual durante load/error; nunca prefetch de dezenas
de backdrops. Essa camada aguarda imagens reais autorizadas, não é reivindicada como implementada.

## Segurança

Não existem JWT, senha, refresh, contas ou endpoints de escrita de perfis neste MVP.
Não inventar testes de auth que não existe. CORS continua fechado por padrão (Android
nativo não depende de CORS). Helmet, Prisma parametrizado, DTO whitelist e rate limit global
120/min preservados. IA tem limite específico 30/min. Produção não aceita DATA_MODE=mock.
URLs demo exigem HTTP(S) sem credenciais; logs usam templates de rota sem query/body.
Segredos/keystores/local.properties ignorados. Debug permite LAN HTTP; release mantém HTTPS.

Auditoria npm após atualização: 8 alertas high no grafo de produção (inclui transitivas
do CLI Prisma); 9 no grafo completo. Restam Multer 2.2.0 (sem endpoints de upload no app),
Effect e DeepmergeTS sob Prisma config (não recebem configuração externa nesta aplicação).
Não aplicado audit fix --force: sugere Nest 12 e downgrade Prisma. Estes riscos não são
declarados resolvidos e exigem nova revisão antes de expor uploads/configuração remota.
Supertest 7.1.1 e ESLint 9.28 também emitem depreciação; cobertura e lint continuam ativos.

## Validação e critérios

| Verificação | Resultado |
| --- | --- |
| Backend build/lint | PASS |
| npm ci e Prisma generate | PASS após encerrar API local que mantinha DLL aberta |
| Build admin Next.js | PASS |
| Backend testes REST | PASS — 10 testes |
| PostgreSQL vazio → migration → seed duas vezes → API | PASS em PostgreSQL isolado local |
| Compose config | PASS |
| Compose up | NÃO EXECUTADO — daemon Docker indisponível |
| Android assembleDebug | PASS |
| Android testes | PASS — 7 testes |
| Android lint | PASS — 0 erros, 18 warnings |
| Runtime emulador M2 | MP4, HLS VOD, DASH VOD, HLS live próprio, erro/retry, Back e diagnóstico observados |
| GitHub Actions M2 | Backend e Android PASS no run 35215468073; correções finais terão novo run |
| TV física / standby / Wi-Fi fabricante / 2h | REQUIRES_PHYSICAL_DEVICE |

Avisos: versões Android mais novas, target SDK, banner, orientação TV, backup, KTX;
clipboard Compose legado funciona mas possui aviso de depreciação. Não ocultados por suppressions.
Uma rodada intermediária compilou testes enquanto fontes mudavam; outra expôs limpeza do
dispatcher de testes antes de cancelar coroutines. Corrigido e suíte reexecutada com sucesso.
O primeiro CI M2 encontrou verificador de banco ainda esperando dataMode no health;
atualizado para o contrato seguro status/database. Nenhum teste desativado.

## Evidência de execução Android TV API 34 x86

Instalação/atualização APK, Home, abertura de canal e filme via D-Pad; Back do player
foca Assistir, Back do detalhe restaura Aurora TV. Falha de API esgota três tentativas
em 2/4/8 segundos e coloca foco em Tentar novamente. Sem crash observado nesse fluxo.

HLS live próprio chegou ao primeiro frame em 2378ms. MP4 VOD: 1058ms; retorno do
background: 1228ms; DASH: 611ms; HLS VOD: 991ms. Valores pontuais do emulador,
não benchmark nem promessa de desempenho. Cada formato VOD emitiu play_completed.
WatchProgress inspecionado com positionMs=20000, durationMs=20000, percentage=100,
completed=true e updatedAt. Nenhuma requisição de progresso por segundo.

Lifecycle observado: player_released em 15:56:34, novo player_created em 15:56:36,
primeiro frame em 15:56:37 (horários UTC do logcat). Não houve instâncias simultâneas
nessa sequência. Um snapshot anterior mostrou PSS ~72MiB; insuficiente para afirmar
estabilidade de memória por duas horas. Gerador live ficar ligado não equivale a player validado 2h.

Configurações → Sobre → Diagnóstico acessível por D-Pad, API Conectado, versão 0.2.0,
Android/modelo e Copiar informações focado; captura em screenshots/android-tv-diagnostics.png.
O emulador manteve conectividade via outra interface ao desligar Wi-Fi; não se reivindica
teste real de perda/retorno de Wi-Fi. Falha de servidor e recuperação foram testadas;
transição de conectividade também coberta por teste de ViewModel. Fabricantes exigem gate físico.

Após sessão prolongada do gerador, segmento HLS expirado retornou 404; recuperação de
janela live ficou limitada ao mesmo orçamento. O gerador passou a reter mais segmentos
e publicar manifesto atomicamente. Também corrigida saída DASH que colocava segmentos
na raiz em vez da pasta da mídia. Diálogos de trilhas são dispensados ao liberar o player.

Não considerar milestone aprovado por teste físico até preencher [resultados](physical-tv-test-results.md).
Progresso/favoritos são locais, sincronização remota não existe. EPG é fictício; nenhum servidor
comercial foi integrado. Duas horas e memória física não foram inferidas de testes curtos.

## Próximos passos

Executar gate físico, corrigir defeitos específicos de hardware e então iniciar M3 mobile
(conta, pairing, remote/teclado/voz, segunda tela). Player mobile/PiP/handoff executável são M4.
Projetos conceituais e decisões em [mobile](mobile-architecture.md) e [ADRs](decisions/002-mobile-realtime-strategy.md).
