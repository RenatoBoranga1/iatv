# IA TV — redesign Android TV

## Escopo e base

Branch: `feature/tv-ui-professional-redesign`.
SHA inicial: `785725c084708da5d8da44f65b79d8db358c5685`.
Checkout: `C:/Users/USER/Documents/Playground/iatv`.
Nenhum merge em main. Provider, autenticação, endpoints, DRM e protocolos do
player não foram alterados. A edição anterior de
`docs/research/brasil-tv-5.50-runtime-environment.md` permanece fora dos commits.

## Arquitetura visual

`MainActivity` inicializa a aplicação. `feature/home/IaTvApp.kt` coordena páginas,
seleção, estado de rolagem e retorno. As telas estão em `feature/details`, `live`,
`search`, `assistant`, `profile` e `settings`.

`core/designsystem/IaTvTheme.kt` centraliza paleta navy/ciano/roxo, tipografia,
formas, espaçamentos e movimento. Vermelho é reservado ao badge AO VIVO.
Foco de 160 ms com escala 1,06 e borda ciano. BringIntoView move somente itens
fora da área visível, evitando esconder o hero ou título ao focar uma ação.

Componentes em `core/ui/components`:
- NavigationRail e TopBar; logo fornecido e marca IA TV no menu expandido.
- Hero com backdrop original, fades, Assistir agora, Minha Lista e Mais informações.
- PosterCard, LandscapeCard, LiveCard e ContinueCard com progresso salvo.
- ContentRow, CatalogGrid, SectionTitle e ações Ver todos com destinos reais.
- ActionButton, Filters, Badge, LoadingCard, EmptyState e ErrorState.
- LocalArtwork com fallback procedural para IDs desconhecidos.

ContentCard recebe `posterUrl`, `backdropUrl`, `thumbnailUrl` e `logoUrl`, todos
opcionais. Não muda DTOs do backend nem contratos compartilhados. Nesta fase,
URLs externas não são baixadas: recursos locais ilustram apenas IDs do mock.

## Referências

As seis referências foram localizadas em Downloads e inspecionadas diretamente.
Elas não são empacotadas no APK nem usadas como telas de fundo. Os pôsteres de
terceiros da referência do Assistente não foram copiados.

| Tela | Referência local | Captura real |
|---|---|---|
| Home | IA TV_ Entretenimento sem limites.png | screenshots/01-home-implementation.png |
| Home/foco | Imagem do ChatGPT 21 de out. de 2026, 08_30_44-5.png | screenshots/02-home-focus-implementation.png |
| Live | Imagem do ChatGPT 21 de out. de 2026, 08_30_39-1.png | screenshots/03-live-implementation.png |
| Details | Imagem do ChatGPT 21 de out. de 2026, 08_30_40-2.png | screenshots/04-details-implementation.png |
| Assistente | Imagem do ChatGPT 21 de out. de 2026, 08_30_42-3.png | screenshots/05-assistant-implementation.png |
| Busca | Imagem do ChatGPT 21 de out. de 2026, 08_30_43-4.png | screenshots/06-search-implementation.png |

As seis capturas de implementação foram atualizadas em 22/10 com o APK local
do código `981bf8f`, em 1920×1080/density 320. `before-home.png` registra a Home
anterior. A validação visual em 720p e 4K permanece pendente.

## Artes e memória

Geradas com o tool nativo image_gen e a skill imagegen. Arquivos em
`apps/android-tv/app/src/main/res/drawable-nodpi/`:

- `iatv_horizon.png` (21/10): cinematic original alien ocean valley, distant
  futuristic towers, solitary explorer at right, vast blue planet, navy/peach/
  cyan/violet atmosphere, dark left half for native text; no text, UI, logos,
  borders or recognizable franchise characters/costumes.
- `iatv_coast.png` (22/10): original cinematic 16:9 rocky Atlantic coast at blue
  hour, solitary lighthouse at right, turquoise surf, navy ocean, gold sunset,
  dark lower quarter and left third. No UI, words, logos, recognizable monument,
  people or copyrighted characters.
- `iatv_neighborhood.png` (22/10): original cinematic 16:9 invented Brazilian
  coastal neighborhood at dusk, colorful low-rise houses, courtyard dinner table,
  bougainvillea, string lights, two distant unidentifiable adults at right,
  warm amber windows, indigo sky and dark lower quarter. No UI, words, brands,
  real landmarks, celebrities or franchise characters.

Decodificação em Dispatchers.IO; amostragem 1/4 nos cards, 1/2 nos backdrops.
Cache LRU compartilhado de 8 MiB; não depende de serviço de imagens externo.
Logo existente amostrado em 1/8. Listas com keys estáveis, linhas da grade
virtualizadas pela LazyColumn, skeleton estático, sem animações contínuas.

## Navegação e dados

Cada página mantém rolagem vertical, cada seção/filtro mantém rolagem horizontal.
Retorno usa sectionId/contentId; destaque tem alvo próprio. Favorito removido
retorna ao menu se o card original não existir. Player retorna a Details ou ao
hero conforme a origem. O app solicita modo de entrada por teclado/D-Pad ao abrir.

Live e Busca usam filtros sobre dados existentes e grades de três colunas.
EPG descarta intervalos inválidos, usa [start,end) e calcula progresso pelo
relógio a cada 30s enquanto lifecycle STARTED. Não inventa horários ou programas.
ContinueCard usa WatchProgress local; não fabrica progresso para completar a Home.

## Validações e ambiente

Emulador IA TV API 34; resolução principal 1920×1080, density 320.
API em DATA_MODE=mock / CONTENT_PROVIDER=mock e mídia própria test-pattern.mp4.
Nenhum aparelho físico ou APK Brasil TV usado no redesign.

Em 21/10, assembleDebug, testDebugUnitTest e lintDebug passaram: 21 testes,
zero falhas; lint zero erros e 18 avisos. O vídeo mock chegou ao primeiro frame
após retry automático e terminou. A restauração Home/card/Details/Back foi
observada no mesmo card e bounds, após correção de BringIntoView.

Em 22/10, o Gradle local parou em java.io.RandomAccessFile.open0 lendo JARs,
antes das tarefas Kotlin. Foi verificada a distribuição e tentada cópia limpa
da mesma versão 8.11.1, sem mudar wrapper/dependências. O workflow existente
executa assemble/test/lint independentemente e publica APK debug por 7 dias
somente após sucesso. A tentativa local chegou posteriormente à tarefa Kotlin,
mas seu resultado final não foi recuperado; não é contabilizada como aprovada.

O CI do commit `0aec66ae0d3cb1323ed3327cea160a836cc91e4b` passou nos jobs
Android e backend em 22/10. Android executou assembleDebug, testDebugUnitTest e
lintDebug, com publicação do APK de debug:
[execução 39772358070](https://github.com/RenatoBoranga1/iatv/actions/runs/39772358070).
Esse resultado valida o código atual, mas não substitui a inspeção visual nem
os testes em TV física.

Commits de implementação publicados na branch:
- `3caaab6`: interface, componentes, navegação, artes e testes Android TV.
- `0aec66a`: publicação do APK de debug após validação no CI.

Avisos conhecidos: versões de AGP/dependências, target API, data extraction,
formato do launcher icon, DiscouragedApi, SwitchIntDef e sugestões UseKtx.
Também existe depreciação de LocalClipboardManager no diagnóstico e aviso de
opções Kapt. Nenhuma regra foi desabilitada para tornar a validação verde.

## Comparação e limites

A implementação usa o catálogo de demonstração existente: dois canais, três
filmes, duas séries e YouTube. Quantidade de itens e densidade diferem das
referências. Não há ratings inventados, posters comerciais ou voz simulada.
O Assistente usa orb abstrato próprio; sugestões e recomendações continuam
ligadas à API existente. O menu recolhe ao sair do foco, preservando área útil.

Teste físico de legibilidade a 2–3m, controle remoto e sessões longas permanece
REQUIRES_PHYSICAL_DEVICE. A equivalência visual integral às referências não é
inferida apenas de build/testes: conferir a matriz e capturas desta revisão.

## Refinamento visual — 22/10, segunda revisão

Commit de implementação: `981bf8f`.

- Cards horizontais mantêm proporção 16:9 também na grade, evitando recortes
  excessivamente baixos quando a largura aumenta. Pôsteres aceitam duas linhas
  de título; badges vazios não deixam um elemento sem texto sobre a imagem.
- Assistente ganha um painel com gradiente discreto, borda e hierarquia entre
  identificação, pergunta principal e sugestões. O orb continua estático.
- Busca mantém a linha de ajuda ao receber/perder foco, evitando deslocamento
  da grade. As ações Buscar/Enviar do teclado também submetem a consulta e
  fecham o teclado.
- Falha de atualização com conteúdo em cache usa aviso compacto com retry.
  Sem conteúdo disponível, permanece a mensagem completa.

Validação local: `assembleDebug testDebugUnitTest lintDebug` concluído com
BUILD SUCCESSFUL em 11m28s. 21 testes, zero falhas/erros; lint com zero erros
e 18 avisos preexistentes. CI Android e backend aprovados para o mesmo commit:
[execução 39817023771](https://github.com/RenatoBoranga1/iatv/actions/runs/39817023771).

APK instalado no emulador API 34. Conferidos: Home; Right do menu para a ação
do hero; Down para Aurora TV; abertura por Select e Back com restauração do
foco no mesmo card; Details com dois relacionados; Live com dois canais e
EPG; Assistente; Busca pelo termo público `mar`, retornando O Som do Mar.
A ação Enter do teclado submeteu a busca e fechou o teclado. A linha de ajuda
e os filtros mantiveram suas posições ao mudar o foco.

Limitação observada: algumas primeiras requisições ao mock retornaram erro de
conexão; retry recuperou a listagem e a busca. A causa não foi determinada
nesta revisão visual. Não foi executado playback nem teste em aparelho físico.

| Tela | Inspeção da revisão | Diferença em relação à referência |
|---|---|---|
| Home | Hero, logo, ações e duas artes de canais visíveis | Catálogo mock menor; menu recolhe sem foco |
| Home/foco | Borda ciano no card e retorno por Back | Menor densidade de títulos |
| Live | Imagem 16:9, programa atual, horários e próximo | Dois canais mock, grade de três colunas |
| Details | Sinopse, ações e relacionados visíveis | Conteúdo e arte originais de demonstração |
| Assistente | Painel, orb, campo e sugestões legíveis | Orb abstrato; resposta abaixo da dobra exige rolagem |
| Busca | Campo, filtros e resultado de `mar` visíveis | Um resultado real do mock, sem preencher a grade artificialmente |

## Baseline recebido do operador

O documento de investigação foi registrado separadamente em
`docs/research/brasil-tv-runtime-gate-analysis.md`. As classificações pertencem
às evidências relatadas pelo operador; não foram reexecutadas nesta etapa.
