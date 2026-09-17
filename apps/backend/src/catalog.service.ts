import { Inject, Injectable, NotFoundException } from "@nestjs/common";
import { ContentCard, HomeResponse } from "@iatv/contracts";
import { Database } from "./database";
import {
  catalog,
  MockSportsProvider,
  normalize,
  sportsCards,
  today,
} from "./providers/catalog";
@Injectable()
export class CatalogService {
  constructor(
    @Inject(Database) private db: Database,
    @Inject(MockSportsProvider) private sports: MockSportsProvider,
  ) {}
  async all(): Promise<ContentCard[]> {
    if (process.env.DATA_MODE === "mock") return catalog;
    return this.db.content.findMany({ orderBy: { id: "asc" } }) as Promise<
      ContentCard[]
    >;
  }
  async get(id: string) {
    const item = process.env.DATA_MODE === "mock" ? catalog.find((c) => c.id === id) : await this.db.content.findUnique({ where: { id } });
    if (!item) throw new NotFoundException("Conteúdo não encontrado.");
    return item;
  }
  async search(q: string) {
    const term = normalize(q.trim());
    return term
      ? (await this.all())
          .filter((c) =>
            normalize(`${c.title} ${c.category} ${c.overview}`).includes(term),
          )
          .slice(0, 50)
      : [];
  }
  async home(): Promise<HomeResponse> {
    const all = await this.all();
    return {
      greeting: "Olá. O que você quer assistir?",
      mock: true,
      sections: [
        { id: "continue", title: "Continuar assistindo", items: [] },
        {
          id: "live",
          title: "Agora na TV",
          items: all.filter((c) => c.kind === "channel"),
        },
        {
          id: "recommended",
          title: "Recomendado pela IA",
          items: all.filter((c) => c.kind === "movie").slice(0, 2),
        },
        {
          id: "movies",
          title: "Filmes para você",
          items: all.filter((c) => c.kind === "movie"),
        },
        {
          id: "series",
          title: "Séries para você",
          items: all.filter((c) => c.kind === "series"),
        },
        {
          id: "sports",
          title: "Jogos de hoje",
          items: sportsCards(await this.sports.getEventsByDate(today())),
        },
        {
          id: "trending",
          title: "Em alta",
          items: all.filter((c) => c.kind === "movie"),
        },
        {
          id: "youtube",
          title: "YouTube",
          items: all.filter((c) => c.kind === "youtube"),
        },
        { id: "favorites", title: "Minha Lista", items: [] },
      ],
    };
  }
}
