# Brasil TV 5.50 — ambiente ARM

Estado em 2026-10-20: **BLOCKED_BY_COMPATIBILITY_GATE**.
J7 SM-G610M observado via ADB: API 27, ARM32, phone, UI normal, sem features
Leanback/television. APK instalado e Home confirmados; gate em Lista de Canais.
SM-T295 API 30 ARM32/64 com mesmo gate relatado pelo operador; sem ADB nesta sessão.
Inventário, logs, codecs, DRM e limites: [diagnóstico físico](brasil-tv-5.50-device-gate.md).
Nenhum Android TV/Fire TV testado. A compatibilidade de TV permanece UNKNOWN.

## Candidato TV — histórico de planejamento

Fire TV Stick 4K 2ª geração (2023), modelo AFTKM, é candidato.
[Amazon — Fire OS](https://developer.amazon.com/docs/fire-tv/fire-os-overview.html)
informa Fire OS 8 / Android 11 / API 30 para esse modelo.
O anúncio fornecido pelo operador não pôde ser verificado; geração, sistema,
ABIs e acesso ADB do produto vendido permanecem NÃO CONFIRMADOS.
Não comprar apenas com base no nome “4K Wi-Fi 6”.
É necessário Android/Fire OS compatível com APK, e suporte ARM32 para os assets
kcptun (caso sejam necessários em runtime). A compatibilidade do binário protegido
e das dependências Google/Firebase no Fire OS ainda precisa ser testada.

## Próximo dispositivo TV — pré-instalação com participação do operador

Após disponibilização do aparelho, habilitar ADB conscientemente no dispositivo
de teste e autorizar este computador. Usar sempre o serial explícito, nunca o
primeiro aparelho da lista. Não publicar IP, serial ou identificadores da conta.
Registrar apenas:

| Item | Resultado no candidato TV ainda não disponível |
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
