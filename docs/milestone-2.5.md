# Milestone 2.5 — Provider Integration
## Etapa atual 2.5.2 — 2026-10-20

**BLOCKED_BY_DEVICE**. Operador ainda comprará Fire TV; geração/sistema serão
confirmados. Pesquisa de 5.2 interrompida por solicitação, mantida como histórico.
SHA inicial desta etapa: afbacb09fa72e3b6b3c0a8a74695e54847459361.
As 11 alterações locais de 2.5.1 foram preservadas no commit ebae6a7.
Branch milestone-2.5-provider-integration; sem merge em main.

APK 5.50 validado read-only: pacote diferente, ARM32/ARM64, assets kcptun ARM32,
ranger-jni, IJK/FFmpeg, shell/proteção e manifest inventariados.
Não instalado. Nenhum login, catálogo, canal ou fonte observado.
Provider permanece fail-closed e mock preservado; não há implementação real
baseada em strings. ADR 003 não mudou: faltam evidências de runtime.

Documentação:
- [Estática e comparação](research/brasil-tv-5.50-static-analysis.md)
- [Ambiente ARM e gate](research/brasil-tv-5.50-runtime-environment.md)
- [Runtime e POCs pendentes](research/brasil-tv-5.50-runtime-analysis.md)

Logo fornecida pelo usuário incorporada sem modificar a arte ao menu,
ícone e banner do Android TV. Imagem local única, sem chamadas de rede;
proporção quadrada preservada dentro do banner 16:9 e do menu.
Sem redesign geral, alteração de navegação ou novos recursos mobile.

Baseline imediatamente anterior às mudanças: backend 14/14, build/lint,
admin build/lint, Android assembleDebug/testDebugUnitTest/lintDebug passaram.
Após a mudança: backend build/lint passaram; primeira repetição de testes
restrita pelo sandbox (EACCES em loopback), repetição autorizada passou 14/14.
Validação final Android: assembleDebug, testDebugUnitTest (7 testes) e lintDebug
passaram em 8m51s, 33 tarefas executadas e 29 up-to-date. Lint: 0 erros,
18 warnings avaliados: atualizações de ferramentas/dependências, target API,
orientação fixa TV, backup, KTX, STATE_IDLE e IconLauncherShape.
O aviso do ícone decorre do fundo opaco da arte fornecida, preservado nesta
entrega; nenhum warning foi suprimido. Recorte/adaptive icon pode ser refinado
numa etapa de identidade visual. Warnings de clipboard/kapt também permanecem.
Instalação e startup do IA TV no AVD API 34 passaram; Home mock e logo sem
deformação conferidas visualmente. Isso não é teste do provider nem de TV física.
APK debug SHA256: 4C06FB0D596249827C04FE5BD91AD956FF26A00EF5E82C2BBDDD1163E6B05891.
Secret scan por padrões e revisão dos arquivos novos: nenhum segredo identificado;
nenhum APK, biblioteca extraída, sessão ou dump bruto incluído no Git.
CI do commit dbd5436: backend aprovado; Android ainda em execução na última consulta
([Actions](https://github.com/RenatoBoranga1/iatv/actions/runs/39463609324)).
A pendência npm audit crítica do Next registrada em 2.5.1 permanece aberta.

## Retomada 2.5.1 — 2026-10-20
RUNTIME_UNBLOCKED na API 29 x86: instalação limpa e login confirmados, segundo
launch COLD ok e processo ativo. Causa provável da API 34: hook de PackageManager
esperando Integer para flags agora Long. Sem patch no APK ou expansão de provider.
Ver [crash](research/brasil-tv-startup-crash.md),
[matriz](research/brasil-tv-compatibility-matrix.md) e
[setup](research/android-legacy-runtime-setup.md).

Gates: investigação de crash/contexto/persistência/manifest/ABI documentada;
compatibilidade e startup/login API 29 aprovados. Native load pré-crash UNKNOWN.
Métodos CAuth/config não instrumentados; catálogo e dois canais pendentes de
sessão de teste. Portanto o critério completo de 2.5.1 (incluindo catálogo)
ainda não foi satisfeito, embora o objetivo de desbloquear startup tenha sido.
Milestone 2.5 e POCs reais continuam incompletos. Mobile não iniciado; sem merge.

Baseline desta retomada: npm ci inicialmente falhou com EPERM ao criar symlinks
de workspace; repetição autorizada fora do sandbox passou. Prisma generate,
build de contracts/backend, lint backend e 14/14 testes passaram. Admin build e
lint (TypeScript) passaram. Android assembleDebug, testDebugUnitTest e lintDebug
passaram (62 tarefas: 1 executada, 61 up-to-date). O coletor de crash foi testado
com dados sintéticos: mantém frames do processo alvo e remove valores arbitrários
e frames de outro processo. A observação estrutural confirmou login vazio na API 29.

Auditoria npm desta retomada: 20 vulnerabilidades reportadas (2 moderadas,
17 altas e 1 crítica). Next 16.3.5 é dependência direta com achado crítico;
a ferramenta aponta correção em 16.4.2. Correção e regressão de dependências
permanecem pendentes; nenhuma atualização foi misturada à investigação de runtime.
Builds verdes não equivalem a ausência de vulnerabilidades.

O usuário optou por preencher o login diretamente no emulador visível. A observação
da interface fica suspensa durante a digitação, até confirmação humana do resultado.
Os resultados abaixo são históricos.

## Histórico da entrega 2.5
Estado: EM ANDAMENTO; POCs reais não executados.
Base: milestone-2-hardening, 7039e4e; branch milestone-2.5-provider-integration.
## Implementado
- Análise read-only do APK 5.2, hash, metadata, bibliotecas e símbolos.
- Evidence ledger, inventário JNI e matriz de contratos sem endpoints inventados.
- ContentProvider/ProviderDeviceIdentity, factory mock|brasiltv.
- BrasilTvProvider fail-closed: fundação, sem cliente remoto funcional.
- PlaybackSource com protocolo/variants/expiração; campos antigos preservados
  para o cliente Media3 existente. Variantes/headers ainda não consumidos pelo Android.
- Erros seguros e helper estrutural que não persiste valores de payloads.
- Health administrativo /v1/admin/providers/brasiltv/health e painel
  /providers/brasiltv. Prefixo /v1 preservado.
## Configuração
CONTENT_PROVIDER=mock é o padrão. brasiltv retorna indisponibilidade,
inclusive catálogo/Home/busca, sem fallback enganoso.
PROVIDER_ADMIN_TOKEN: segredo operacional aleatório de pelo menos 32 caracteres,
provisionado fora do Git. Ausente ou inválido => 401. Usar HTTPS fora de loopback.
IATV_BACKEND_URL é server-only no admin. Credencial informada pelo operador,
sem persistência no browser, sem credencial automática em rota pública.
Este mecanismo provisório não implementa contas/roles.
## Testes
Baseline backend: 10/10. Validação final: 14/14, lint e build passaram.
Admin lint/build passaram em 21/09. Navegador: página renderiza, credencial inválida mostra acesso necessário, zero erros no console. agent-browser CLI não disponível; verificação feita com navegador integrado.
Android: assembleDebug, testDebugUnitTest e lintDebug passaram em 21/09 (tarefas incrementais, 1 executada/61 up-to-date). Sem alterações Android nesta fase.
## Gates
- 2.5A: metadata/símbolos mapeados; payload protegido/comportamento pendentes.
- 2.5B: fundação implementada, mock preservado.
- 2.5C: REQUIRES_PROVIDER_CONTRACT.
- 2.5D: APK instalado e aberto; crash Long→Integer em SopCast.onCreate na API 34 impede auth/catalog/player. Usuário autorizou Install anyway apenas no AVD isolado.
- 2.5E: live/filme/episódio NÃO executados; milestone não concluído.
## Limitações
Sem mapper validado, auth/session/broker remotos, suporte completo a variantes,
DRM ou séries/episódios reais. Métodos mock de temporadas/episódios retornam vazio
porque não há fixtures normalizadas desses recursos.
Nenhum segredo extraído, token forjado ou mecanismo de limites contornado.
## Próxima evidência
Confirmar launch, sessão autorizada, dois canais, URI/formato; implementar somente
contratos observados, executar live 10 minutos e depois filme/episódio.

## Publicação e CI
Branch publicada em 21/09: milestone-2.5-provider-integration.
GitHub Actions no commit 132f3fd: backend e Android SUCCESS.
Execução: https://github.com/RenatoBoranga1/iatv/actions/runs/35594246468
Varredura de segredos revisada: nomes de contratos, marcadores de redação e credenciais sintéticas de teste; nenhum segredo real incluído. APK, capturas e dados de runtime ficaram fora do Git.

