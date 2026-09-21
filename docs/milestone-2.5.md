# Milestone 2.5 — Provider Integration
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


