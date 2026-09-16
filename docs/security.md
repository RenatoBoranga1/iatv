# Segurança e limites da demonstração

- Variáveis de ambiente para configuração; `.env` ignorado. Nenhuma chave de IA ou segredo em APK.
- Validação de DTO com whitelist e rejeição de campos extras; mensagens limitadas a 500 caracteres.
- Helmet e rate limit no backend. Prisma para acesso parametrizado ao banco. Log HTTP estruturado usa template da rota, status e duração, sem query, corpo ou tokens. Erros inesperados retornam mensagem genérica.
- Sem endpoints de escrita administrativa, credenciais de usuário ou pagamentos.
- Acesso público intencional apenas ao catálogo fictício; não publicar dados reais neste servidor de demonstração.
- HTTP debug para rede local; release precisa de HTTPS.
- O banco Docker é publicado em localhost. Senha fornecida pelo operador; não usar o placeholder do `.env.example` em produção.

Pendências antes de produção: auth/refresh/QR, autorização por perfil, gestão de dispositivos, auditoria, observabilidade com correlação de erros, CORS conforme clientes web, TLS, política de backup, limitação distribuída quando houver múltiplas instâncias e revisão de dependências.
