import { Inject, Injectable } from "@nestjs/common";
import { AssistantResponse, ContentCard, Intent } from "@iatv/contracts";
import { CatalogService } from "./catalog.service";
import {
  MockSportsProvider,
  normalize,
  sportsCards,
  today,
} from "./providers/catalog";
export interface AiProvider {
  classifyIntent(message: string): Intent;
  extractFilters(message: string): { genre?: string; maxMinutes?: number };
  chat(message: string): Promise<AssistantResponse>;
  recommend(message: string): Promise<ContentCard[]>;
}
@Injectable()
export class AuthorizedTools {
  constructor(
    @Inject(CatalogService) private catalog: CatalogService,
    @Inject(MockSportsProvider) private sports: MockSportsProvider,
  ) {}
  async searchMovies(filters: { genre?: string; maxMinutes?: number }) {
    return (await this.catalog.all()).filter(
      (c) =>
        c.kind === "movie" &&
        (!filters.genre || normalize(c.category) === filters.genre) &&
        (!filters.maxMinutes || c.durationMinutes < filters.maxMinutes),
    );
  }
  async getTodayGames() {
    return sportsCards(await this.sports.getEventsByDate(today()));
  }
  async getLiveGames() {
    return sportsCards(await this.sports.getLiveEvents());
  }
}
@Injectable()
export class MockAiProvider implements AiProvider {
  constructor(@Inject(AuthorizedTools) private tools: AuthorizedTools) {}
  classifyIntent(message: string) {
    const q = normalize(message);
    if (/jogo|partida/.test(q))
      return /agora|ao vivo/.test(q) ? Intent.LIVE_GAMES : Intent.TODAY_GAMES;
    if (/filme|comedia|familia|ficcao|recomende/.test(q))
      return Intent.SEARCH_MOVIE;
    return Intent.HELP;
  }
  extractFilters(message: string) {
    const q = normalize(message);
    return {
      genre: q.includes("comedia")
        ? "comedia"
        : q.includes("ficcao")
          ? "ficcao cientifica"
          : undefined,
      maxMinutes: /duas horas|2 horas/.test(q) ? 120 : undefined,
    };
  }
  recommend(message: string) {
    return this.tools.searchMovies(this.extractFilters(message));
  }
  async chat(message: string): Promise<AssistantResponse> {
    const intent = this.classifyIntent(message);
    const cards =
      intent === Intent.TODAY_GAMES
        ? await this.tools.getTodayGames()
        : intent === Intent.LIVE_GAMES
          ? await this.tools.getLiveGames()
          : intent === Intent.SEARCH_MOVIE
            ? await this.recommend(message)
            : [];
    return {
      intent,
      cards,
      mock: true,
      message:
        intent === Intent.TODAY_GAMES
          ? `Encontrei ${cards.length} partidas fictícias para hoje.`
          : intent === Intent.LIVE_GAMES
            ? "Nenhuma partida ao vivo na agenda demonstrativa."
            : intent === Intent.SEARCH_MOVIE
              ? `Separei ${cards.length} opções do catálogo fictício para você.`
              : "Sou o assistente demonstrativo. Experimente “Quais jogos têm hoje?” ou “Quero uma comédia”.",
    };
  }
}
