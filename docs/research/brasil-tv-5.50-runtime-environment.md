# Brasil TV 5.50 — ambiente ARM

Retomada em 2026-10-20: operador informou Fire TV disponível e autorizou
identificação e instalação, primeiro IA TV e depois Brasil TV 5.50.
HEAD inicial confirmado: 785725c084708da5d8da44f65b79d8db358c5685,
branch milestone-2.5-provider-integration, working tree inicialmente limpo.
**BLOCKED_BY_DEVICE** neste momento significa conexão ADB pendente:
`adb devices -l` só encontrou o Samsung, nenhum Fire TV. Foi solicitado ao
operador habilitar ADB e informar endereço local de conexão; nenhum IP será
versionado. A consulta precisou executar fora do sandbox para acessar o servidor ADB.
O Samsung conectado anteriormente não foi selecionado nem modificado.
O emulador do 5.2 não será usado para executar este APK ARM.

Os dois APKs locais foram encontrados e seus SHA-256 revalidados:
- IA TV: 4C06FB0D596249827C04FE5BD91AD956FF26A00EF5E82C2BBDDD1163E6B05891.
- Brasil TV: DD7F35A5949BECE00704BDF7D9D7A7431945F2DA5A78F873564061A38F81BA88.

Nenhuma instalação ou captura de tela foi executada nesta retomada.

## Candidato

Fire TV Stick 4K 2ª geração (2023), modelo AFTKM, é candidato.
[Amazon — Fire OS](https://developer.amazon.com/docs/fire-tv/fire-os-overview.html)
informa Fire OS 8 / Android 11 / API 30 para esse modelo.
O anúncio fornecido pelo operador não pôde ser verificado; geração, sistema,
ABIs e acesso ADB do produto vendido permanecem NÃO CONFIRMADOS.
Não comprar apenas com base no nome “4K Wi-Fi 6”.
É necessário Android/Fire OS compatível com APK, e suporte ARM32 para os assets
kcptun (caso sejam necessários em runtime). A compatibilidade do binário protegido
e das dependências Google/Firebase no Fire OS ainda precisa ser testada.

## Pré-instalação com participação do operador

Após disponibilização do aparelho, habilitar ADB conscientemente no dispositivo
de teste e autorizar este computador. Usar sempre o serial explícito, nunca o
primeiro aparelho da lista. Não publicar IP, serial ou identificadores da conta.
Registrar apenas:

| Item | Resultado atual |
|---|---|
| Fabricante/modelo real | UNKNOWN |
| Fire OS / Android / API | UNKNOWN |
| ro.product.cpu.abilist / abilist32 | UNKNOWN |
| Arquitetura kernel | UNKNOWN |
| ADB autorizado | NÃO VERIFICADO |
| Conectividade | NÃO VERIFICADA |
| Espaço em /data | UNKNOWN |
| Resolução | UNKNOWN |
| Instalação / startup | NÃO EXECUTADOS |

Comandos de leitura para o aparelho escolhido (substituir `<serial>` localmente):

```text
adb -s <serial> shell getprop ro.product.manufacturer
adb -s <serial> shell getprop ro.product.model
adb -s <serial> shell getprop ro.build.version.release
adb -s <serial> shell getprop ro.build.version.sdk
adb -s <serial> shell getprop ro.build.version.name
adb -s <serial> shell getprop ro.product.cpu.abilist
adb -s <serial> shell getprop ro.product.cpu.abilist32
adb -s <serial> shell uname -m
adb -s <serial> shell df -h /data
adb -s <serial> shell wm size
```

Validar rede via estado de conexão e resposta do app, sem coletar SSID/MAC.
Instalação e login aguardam o operador. Sem root, patch, alteração de assinatura
ou contorno de DRM/entitlement. Credenciais serão digitadas no aparelho;
nenhum dump/screenshot durante a digitação.
