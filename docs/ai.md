# Assistente IA TV

`MockAiProvider` classifica texto por regras, extrai gênero/duração e aciona somente `AuthorizedTools`. Respostas carregam `message`, `intent`, `cards` e `mock`. O frontend usa o mesmo formato para qualquer provider futuro.

Experimentar: “Quais jogos têm hoje?”, “Tem jogo agora?”, “Quero uma comédia”, “Quero um filme com menos de duas horas”. Fora das intenções suportadas, o assistente explica suas capacidades. Não simula um modelo generativo real.

Não existe acesso a SQL, execução de código, URLs arbitrárias ou alteração de contas. `OpenAiProvider` é uma evolução planejada, ainda não implementada. Chaves e chamadas reais deverão permanecer no backend.
