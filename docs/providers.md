# Providers

Contratos atuais em `apps/backend/src/providers/catalog.ts`: MovieProvider, SeriesProvider, LiveTvProvider, SportsProvider, YouTubeProvider e MetadataProvider. Implementações mock independentes de fornecedores comerciais. Contrato AiProvider em `ai.service.ts`.

Nesta fase os contratos incluem apenas operações utilizadas ou preparadas na demonstração; métodos de temporadas, playlists, times e emissoras serão adicionados ao implementar esses fluxos. O catálogo persistido é acessado pelo CatalogService. A TV conhece DTOs REST, não provedores externos.

Para um provedor real: definir direitos e território, implementar o contrato, normalizar DTOs, testar indisponibilidade e limites; resolver fontes exclusivamente no backend. Nunca incluir endpoints privados ou tokens de fornecedores nas telas.
