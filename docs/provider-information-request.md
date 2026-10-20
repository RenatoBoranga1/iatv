# Informações necessárias do provider
## Gate atual — 5.50: BLOCKED_BY_DEVICE

O operador ainda comprará o Fire TV e confirmará geração e Fire OS com o vendedor.
Ver [ambiente ARM](research/brasil-tv-5.50-runtime-environment.md).
Não instalar no dispositivo físico anteriormente conectado sem seleção do operador.
Depois de validar o aparelho, login será preenchido diretamente nele.
Precisamos observar categorias, dois canais autorizados, channel/resource,
resolver e fonte final. Não solicitar credenciais no chat nem reaproveitar
supostos contratos CAuth/TVCore da versão 5.2.
Se runtime não revelar integração suportada, solicitar SDK/documentação e
credenciais de homologação provisionadas localmente: BLOCKED_BY_PROVIDER_INFORMATION.
VOD, mobile e demais plataformas não fazem parte do escopo atual.

## Histórico 5.2 — encerrado como referência

## BLOCKED_BY_PROVIDER_INFORMATION
Falta conta de teste utilizável ou documentação de integração com credenciais
provisionadas localmente. A autorização de análise foi recebida; não se pede nova
prova de autorização. Não enviar senhas ou tokens no chat/Git.

Necessário: endpoint de homologação, método/content-type/schema de auth,
lifecycle/expiração, campos reais de dispositivo, entitlements e limites,
catálogo com dois canais de teste, relação channel/resource, broker/resolver,
um filme e um episódio autorizados. Preferível SDK/documentação oficial do core
e condições de distribuição em Android/mobile/Tizen/webOS.
Essas informações liberam clientes auth/live/VOD/broker e POCs reais.
## Runtime desbloqueado em 2026-10-20
API 29 x86 executa o APK original e apresenta login, inclusive após relaunch.
O bloqueio de startup permanece específico da API 34 testada; não impede mais
a pesquisa usando API 29. Não é necessário alterar autenticação ou fabricar sessão.
Próximo insumo: conta de teste provisionada localmente ou login humano no AVD
de pesquisa. Sem isso, não enviar tentativa com credenciais inventadas nem
declarar catálogo/TVCore alcançados. Primeiro frame pertence à etapa posterior.
