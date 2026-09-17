# Mobile — arquitetura futura

M2 documenta contratos; não implementa aplicativo, QR, login, socket, voz ou PiP.
M3: controle remoto, teclado, microfone, conta, descoberta/pareamento, realtime e segunda
tela. M4: player móvel, PiP e handoff. Compartilhar DTOs TypeScript em packages/contracts;
Android mantém modelos Kotlin próprios e testes de contrato. API existente /v1 permanece.

## Modelos conceituais

| Modelo | Campos e invariantes |
| --- | --- |
| PairingSession | id, deviceId, codeHash, status(PENDING/APPROVED/EXPIRED/REVOKED), expiresAt, createdAt, approvedAt; código aleatório de uso único, nunca persistir token em claro |
| DeviceSession | id, accountId, deviceId, status, activeControllerId, leaseExpiresAt, lastSeenAt, revokedAt; IDs aleatórios internos, sem serial/MAC |
| PlaybackSession | id, profileId, deviceSessionId, contentId, positionMs, durationMs, state, revision, updatedAt; apenas dono pode escrever |

TV solicita sessão temporária, exibe QR/código; mobile autenticado escaneia, usuário
confirma o nome da TV, backend valida conta, expiração e uso único, aprova e emite
credenciais restritas à sessão. Limitar tentativas, expirar em poucos minutos, invalidar
ao logout/revogação. Não utilizar o código curto como token permanente de controle.

## Controle e concorrência

WebSocket relay conforme ADR 002. Sessão possui proprietário; um controlador ativo
obtém lease curta renovada por heartbeat. Segundo controlador precisa de transferência
explícita. Desconexão expira lease; revogação invalida imediatamente. Cada comando contém
protocolVersion=1, commandId, sessionId, sequence, issuedAt, expiresAt e payload tipado.
Validar tamanho, conta, perfil, sessão, sequência e expiração; ACK informa accepted/rejected.

Allowlist: UP/DOWN/LEFT/RIGHT/SELECT/BACK, PLAY/PAUSE/SEEK(positionMs),
OPEN_CONTENT/OPEN_CHANNEL(contentId), SEARCH(query <=120), AI_COMMAND(text <=500).
Não armazenar textos privados em telemetria. Dispositivo destinatário rejeita versão
desconhecida; o servidor nunca converte payload arbitrário em comando de sistema.

## Handoff TV ↔ mobile

1. Origem salva snapshot final e solicita transferência para dispositivo da mesma conta.
2. Backend compara revision, valida perfil/direitos/concorrrência e cria handoff temporário.
3. Destino resolve novamente a fonte autorizada, prepara e confirma posição + primeiro frame.
4. Backend confirma transferência e manda origem pausar/encerrar; lease passa ao destino.
5. Se destino falhar ou expirar, origem continua e a transferência é cancelada; ACKs duplicados
   são idempotentes. Não parar origem antes de confirmar destino.

No live transferir canal/programa e intenção de estar na borda live; não reaplicar offset
VOD fora da janela do manifesto. Offline: progresso local com versão pendente; estratégia
de conflito deverá considerar sessão/revisão, não apenas relógios dos aparelhos.

M2 continua com perfil local único, sem sincronização remota. Autenticação, refresh token
rotativo armazenado por hash, autorização por objeto e revogação são pré-requisitos de M3.
