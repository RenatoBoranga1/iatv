# Validação do milestone 1

Executada em 16/09/2026, Windows, Node 24, JDK 21 e SDK Android 35.

| Verificação | Resultado |
| --- | --- |
| Prisma generate | Aprovado |
| Build TypeScript do backend e contratos | Aprovado |
| ESLint e typecheck do backend | Aprovado |
| Testes REST NestJS | 7 aprovados, 0 falhas |
| PostgreSQL isolado | Migration aplicada; seed executado duas vezes; 8 itens sem duplicação |
| API com PostgreSQL | Home retornou catálogo persistido |
| Streaming HTTP | MP4 próprio servido com HTTP Range, resposta 206 |
| Build Next.js | Aprovado |
| Typecheck Next.js | Aprovado |
| Android assembleDebug | Aprovado; APK em apps/android-tv/app/build/outputs/apk/debug/app-debug.apk |
| Android testDebugUnitTest | 1 teste aprovado, 0 falhas |
| Android lintDebug | 0 erros, 17 avisos |
| Docker Compose config | Sintaxe aprovada |

Teste Android cobre a separação de dados esportivos e autorização de reprodução e o horário de São Paulo. Testes REST cobrem Home, busca, agenda, AI tool routing, DTOs, playback indisponível e EPG.

Os avisos Android incluem versões mais novas de bibliotecas/Gradle/target SDK, tamanho do banner vetorial, orientação fixa da TV, regras de backup e sugestões KTX. Não foram ocultados por baseline.

O Docker daemon não estava ativo; os containers não foram executados. Para validar o banco foi usado um PostgreSQL 18 temporário isolado em `.tools`, encerrado após o teste; o Compose está configurado para PostgreSQL 17. A equivalência do Compose completo deve ser validada no ambiente com Docker ativo.

O emulador de celular disponível não completou o boot. A validação de execução em Android TV está sendo tratada separadamente do build. A cobertura automatizada ainda não inclui testes de ViewModel, navegação instrumentada ou player real em TV física.
