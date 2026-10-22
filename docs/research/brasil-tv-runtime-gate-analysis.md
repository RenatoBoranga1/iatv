# Baseline observacional — gate Brasil TV

Registrado em 2026-10-22 a partir do relatório fornecido pelo operador.
As classificações abaixo pertencem às evidências relatadas pelo operador; estes testes não foram reexecutados nesta etapa de redesign. A confirmação independente neste ambiente permanece pendente.

Escopo preservado: provider fail-closed, sem alterar autenticação, endpoints, contratos, propriedades dos aparelhos ou mecanismos de proteção. Próximo teste: comparação observacional em Android TV/Google TV físico.

## 1. APK BRASIL TV 5.49.1

APK:
brav_5.49.1 (1).apk

Package:
com.interactive.brasiliptv

versionName:
5.49.1

versionCode:
54901

minSdk:
19

targetSdk:
33

compileSdk:
34

Launcher:
com.interactive.brasiliptv.ui.activity.WelcomeActivity

O APK possui:
- LAUNCHER
- LEANBACK_LAUNCHER

Manifest:
- android.software.leanback = NOT REQUIRED
- android.hardware.touchscreen = NOT REQUIRED
- android.hardware.screen.landscape = REQUIRED
- android.hardware.wifi = REQUIRED
- android.hardware.type.television não é exigido

Conclusão:
CONFIRMED: o APK foi projetado para poder instalar em dispositivos que não são
Android TV. O bloqueio observado é uma decisão em runtime e não do Package Manager.


## 2. GATE OBSERVADO

No Lenovo TB310FU o aplicativo:
- instala normalmente;
- inicia WelcomeActivity;
- chega à HomeActivity;
- depois apresenta diálogo:

Título:
"Dicas"

Mensagem:
"Seu dispositivo não é compatível com este aplicativo."

Botão:
"Sair"

Layout/resource já identificado:
dialog_not_support_tip

String:
account_device_not_support

CONFIRMED:
o gate pertence ao próprio aplicativo.


## 3. LENOVO TB310FU

Android 13
API 33
ARM64 + ARM32

ro.product.model:
TB310FU

ro.product.manufacturer:
LENOVO

ro.product.device:
TB310FU

ro.build.characteristics:
tablet

Features:
- touchscreen presente
- Leanback ausente
- android.hardware.type.television ausente

UI mode:
NORMAL, não TELEVISION.

Portanto:

CONFIRMED:
o Lenovo é identificado pelo Android como tablet, não como Android TV.


## 4. OUTROS DISPOSITIVOS TESTADOS

Samsung SM-T295:
mesmo gate.

Samsung J7 SM-G610M:
mesmo gate.

Lenovo TB310FU:
mesmo gate.

Até o momento todos os dispositivos testados são telefone/tablet,
não Android TV físico.

UNKNOWN:
se a ausência do perfil TV é a causa direta do gate.


## 5. RUNTIME / PROTEÇÃO

O aplicativo é protegido por Ijiami.

Arquivos no APK:

assets/ijm_lib/armeabi/libexecmain.so
assets/ijm_lib/armeabi/libexec.so
assets/ijm_lib/arm64-v8a/libexecmain.so
assets/ijm_lib/arm64-v8a/libexec.so
assets/ijiami.dat
assets/ijiami.ajm

Em runtime são carregados:
libexec.so
libexecmain.so

Também foram observados quatro Anonymous-DexFile.

CONFIRMED:
há código protegido/dinâmico não disponível diretamente no classes.dex público.


## 6. LIBEXEC

Análise estática do libexec ARM64 encontrou:

- ptrace
- /proc/self/maps
- /system/build.prop
- ro.build.version.sdk=
- dlopen/dlopen_ext
- app_process64
- linker64

Não foram encontrados em texto simples:
- model
- manufacturer
- fingerprint
- deviceid
- zui
- SwlNativeService
- mikrom
- qemu

Conclusão:

HIGH_CONFIDENCE:
libexec é principalmente loader/protetor e faz inspeções básicas do ambiente.

LIKELY:
a lógica específica do gate está acima dele, no código protegido carregado
dinamicamente.


## 7. RANGER

libranger-jni.so ARM64 e ARM32 contém componentes C++ reais.

Strings/classes identificadas:

ApiServer
AuthEncrypter
StreamClient
PlayStation
HlsClient
PlayMedia
PlayReader
PlayStream
HttpClient
SecHttpClient
HttpMp4Client
DnsCache
DohClient
SlbProtocol
SlbV10Protocol
SlbV13Protocol
TcpTransport
CdpTransport
WebSocket
StunClient
IceServer

DoH:

Content-Type: application/dns-message
/dns-query

Servidores presentes em strings:
dns.alidns.com
dns10.quad9.net
cloudflare-dns.com

Conclusão:

CONFIRMED:
Ranger fornece infraestrutura nativa de rede, DNS/DoH, HTTP seguro e mídia.

HIGH_CONFIDENCE:
Ranger participa das comunicações funcionais do aplicativo.

UNKNOWN:
se Ranger decide o gate.

Não foi encontrada uma estrutura clara de identidade Android dentro do Ranger:
android_id, imei, serial, manufacturer, model, fingerprint etc. não aparecem
como strings úteis.


## 8. CHECKS DE AMBIENTE OBSERVADOS

Durante startup foram observadas tentativas relacionadas a:

qemu.hw.mainkeys
qemu.sf.lcd_density
mikrom

Também:

org.repackage.com.zui.deviceidservice/.DeviceidService

Resultado no Lenovo:
serviço não encontrado.

Também várias tentativas de:
SwlNativeService

Resultado:
SELinux denied/find.

Esses checks acontecem cerca de 4 segundos ANTES da HomeActivity.

Conclusão:

CONFIRMED:
há uma fase inicial de inspeção/identidade/ambiente.

UNKNOWN:
se a falha desses serviços causa posteriormente o gate.


9. SEQUÊNCIA DE REDE REPRODUZÍVEL

Com log de resolver verbose, foi encontrada uma sequência extremamente consistente.

Execuções independentes:

HomeActivity
~54-56 ms depois:
esyte.jydfb8gtg.com

~195-209 ms depois da Home:
pre.itgfgdz.com
qlgof.hyazjrvos.com

~553-567 ms depois da Home:
segunda janela da HomeActivity recebe foco.

A segunda janela foi confirmada por uiautomator como o diálogo:
"Seu dispositivo não é compatível com este aplicativo."

Esse padrão se repetiu diversas vezes.

yumao.puata.info também aparece no startup, mas em uma execução apareceu
somente depois do gate.

Portanto:

CONFIRMED:
yumao não é requisito temporal necessário para o gate.

CONFIRMED:
esyte, pre e qlgof aparecem de forma altamente reproduzível imediatamente
antes da segunda janela.

HIGH_CONFIDENCE:
esses três hosts fazem parte do fluxo funcional de startup da Home.

UNKNOWN:
se algum deles participa da decisão de autorização/compatibilidade.

Não inferir causalidade apenas pelo timing de DNS.


## 10. HOSTS NÃO ESTÃO HARDCODED EM TEXTO SIMPLES

Foi feita busca ASCII e UTF-16 em todas as entradas do APK por:

jydfb8gtg
itgfgdz
hyazjrvos
puata
esyte
qlgof
yumao

Nenhuma ocorrência.

Portanto:

LIKELY:
esses endpoints são fornecidos/configurados dinamicamente, construídos em
runtime ou armazenados em formato protegido/codificado.

Ainda UNKNOWN qual dessas opções é a correta.


## 11. TESTE OFFLINE

Sem conexão, em vez do gate o app mostra:

"EE1-Internet anormal, verifique a sua rede"

Portanto:

CONFIRMED:
conectividade é necessária para avançar até a fase em que o gate aparece.

UNKNOWN:
se a decisão final de compatibilidade vem do servidor ou é local.


## 12. HIPÓTESE ATUAL

As hipóteses que permanecem são:

A) validação de perfil/form factor local;
B) validação/autorização remota;
C) combinação das duas.

Atualmente C acomoda melhor os dados observados, mas NÃO está confirmada.

Não afirmar que:
"ausência de Leanback causa o bloqueio".

Leanback é apenas uma variável candidata.


## 13. PRÓXIMO TESTE

NÃO continuar desmontando libexec ou tentando contornar proteção.

O próximo teste planejado será SOMENTE em um aparelho físico Android TV /
Google TV.

Quando o hardware estiver disponível, comparar:

- ro.build.characteristics
- ro.product.model
- ro.product.manufacturer
- ro.product.device
- ro.build.version.sdk
- android.software.leanback
- android.hardware.type.television
- touchscreen
- UI mode

Depois instalar exatamente a mesma versão do Brasil TV e verificar:

1. se chega à HomeActivity;
2. se apresenta o mesmo gate;
3. se a sequência DNS esyte -> pre/qlgof também ocorre;
## 4. se os checks DeviceidService / SwlNativeService mudam.

Não alterar propriedades do aparelho para tentar ultrapassar o gate.
O objetivo é exclusivamente comparação observacional.
