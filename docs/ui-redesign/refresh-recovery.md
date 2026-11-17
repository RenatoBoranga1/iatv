# Retomada e estabilidade — 2026-11-17

Código recuperado da branch `feature/tv-ui-professional-redesign`, base
`42976d5`. A cópia anterior não estava mais no caminho local. Nenhum backup,
emulador ou dependência volumosa foi recriado; a validação usa GitHub Actions.

## Correção

Antes, cada atualização apagava as seções imediatamente. Ao falhar uma busca
ou uma listagem, os cards desapareciam, mesmo que a mesma consulta já tivesse
carregado com sucesso.

Agora, uma atualização da mesma página, consulta e data preserva seus cards
durante o carregamento e após falha. O aviso compacto existente permite tentar
novamente sem substituir todo o catálogo. O skeleton completo só aparece
quando não há itens. Uma nova consulta ou outra página limpa os resultados
anteriores imediatamente para não exibir conteúdo sob o título errado.

O estado é atualizado antes de despachar a coroutine, protegendo também duas
solicitações consecutivas antes de a primeira começar a executar.

## Testes adicionados

- Cards preservados durante atualização e falha de rede; retry bem-sucedido.
- Nova consulta não herda resultados da anterior, incluindo duas chamadas
  consecutivas antes da execução da coroutine.
- Troca de página não apresenta os resultados da busca anterior após erro.

Os testes existentes de cancelamento e recuperação de rede continuam aplicáveis.

## Validação

CI aprovado para `789e6fcf294d1a70835c00475bd51a2f05d538c3`:
[execução 44116717409](https://github.com/RenatoBoranga1/iatv/actions/runs/44116717409).
Android: assembleDebug, testDebugUnitTest, lintDebug e publicação do APK.
Backend: instalação, geração Prisma, build, lint, testes, migrations, seed e
verificação do banco. Não houve build ou execução do app local nesta retomada.

## Limites

Esta correção trata a perda de conteúdo na interface, não comprova a causa
das falhas intermitentes de conexão observadas em outubro. Não foi alterado
o provider, o backend, a política de retry HTTP ou o protocolo de playback.
As capturas de outubro não foram regeneradas nesta retomada. Verificações
visuais em 720p/4K e uso em TV física continuam pendentes.

Foi identificado por leitura ADB um Samsung SM-G610M/API 27, sem instalação,
captura de tela ou leitura de dados pessoais. Em 17/11 o operador informou
que o Fire Stick ainda não chegou e preferiu aguardar. Não instalar no Samsung;
retomar a validação física quando o Fire Stick estiver disponível.
