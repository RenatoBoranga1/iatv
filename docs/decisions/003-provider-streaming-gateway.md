# ADR 003 — Integração de streaming do provider
Status: PROVISÓRIO, aguardando runtime. Nenhum gateway implementado.
## Comparação
| Aspecto | A: core no cliente | B: gateway no servidor | C: controle no servidor, vídeo provider/cliente |
|---|---|---|---|
| Banda IA TV | baixa | soma dos bitrates por espectador | baixa |
| Latência | depende do core/peers | salto adicional | caminho direto |
| Escala | distribuída | egress/CPU por sessão | controle central leve |
| Licença | redistribuição do SDK requerida | execução/redistribuição no servidor requerida | contratos de API e acesso direto |
| Segurança | sessão/dispositivo no cliente | credenciais e mídia centralizadas | escopo por sessão/dispositivo |
| Android/mobile | depende de ABIs/SDK | HTTP compatível se autorizado | Media3 se fonte compatível |
| Tizen/webOS | core nativo pode não existir | formato web compatível se autorizado | depende do formato/DRM oferecido |
## Direção
Investigar C primeiro se o provider entregar fontes consumíveis diretamente.
Não é decisão final: hipótese de gateway local ainda não confirmada.
A requer SDK e lifecycle comprovados; B só após necessidade comprovada,
capacidade, custos e autorização específica de distribuição.
Nenhum proxy central de vídeo será criado apenas para esconder URLs.
## Gate de decisão
Observar URI/MIME final, binding, sessão, DRM quando houver e APIs oficiais.
Atualizar esta ADR após esses resultados; não desabilitar DRM.

