# Matriz de foco e D-Pad

Executar cada linha com setas/OK/Enter/Back, sem toque. Registrar resultado, build e vídeo
no teste físico. Esta matriz descreve expectativa, não aprovação automática.

| Tela | Origem | Ação | Foco esperado |
| --- | --- | --- | --- |
| Home | Abertura | Aguardar carga | Início no menu, foco visível |
| Home | Menu | Right | Primeiro card disponível |
| Menu | Qualquer item | Up/Down/OK | Próximo item, rota correspondente |
| Menu | Último item | Down | Assistente IA permanente |
| Filmes/Séries/TV/YouTube | Carrossel | Left/Right | Card adjacente, scroll acompanha |
| Catálogo | Card fora do começo | OK, Back | Mesmo card e scroll |
| Detalhe | Conteúdo reproduzível | OK | Assistir; depois controles do player |
| Detalhe | Sem transmissão | Entrar | Minha Lista; botão indisponível não recebe foco |
| Player | Controles | Back | Detalhe e ação Assistir |
| Player | Vídeo | Esperar 5s, seta | Overlay some e reaparece |
| Player | Áudio/legenda/qualidade | OK, Back | Diálogo e controle de origem |
| Player | Falha definitiva | Aguardar | Tentar novamente visível e focado |
| Minha Lista | Último favorito | Remover, Back | Estado vazio, fallback para menu |
| Busca | Menu | Right/OK | Campo editável pelo controle/teclado do sistema |
| Busca | Campo | Enviar | Resultados ou mensagem vazia, sem trap |
| IA | Sugestão | OK | Resultado, sem perder menu |
| Jogos | Dia anterior/próximo | OK | Atualiza data preservando botão |
| Offline/erro | Tela | Retry | Carregamento e retorno navegáveis |
| Configurações | Sobre | OK | Diagnóstico |
| Diagnóstico | Copiar | OK | Confirmação do clipboard do sistema, sem secrets |
| Todas | Android Home | Voltar ao app | Mesma rota/conteúdo; um player no máximo |
| Home | Card removido/concluído | Back | Menu como fallback, sem foco invisível |

TvAction usa estados TV Material; TvCard tem borda mint de foco, estado pressionado,
disabled e loading. Animação não depende de imagens ou backdrops. Validar distância
de 2–3 metros, overscan, teclas Enter e OK de controles de fabricantes diferentes.
