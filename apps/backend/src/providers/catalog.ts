import { Injectable } from "@nestjs/common";
import {
  ContentCard,
  ContentKind,
  EpgProgram,
  SportsEvent,
} from "@iatv/contracts";

export const catalog: ContentCard[] = [
  {
    id: "movie-orbita",
    kind: "movie",
    title: "Além da Órbita",
    overview:
      "Uma cartógrafa encontra um novo caminho entre as estrelas. Obra fictícia do catálogo demonstrativo.",
    category: "Ficção científica",
    durationMinutes: 108,
    badge: "2026 • 12 anos",
    playable: true,
  },
  {
    id: "movie-domingo",
    kind: "movie",
    title: "Domingo em Família",
    overview:
      "Um almoço, três gerações e um plano que nunca dá certo. Comédia fictícia para compartilhar.",
    category: "Comédia",
    durationMinutes: 92,
    badge: "2026 • Livre",
    playable: true,
  },
  {
    id: "movie-mar",
    kind: "movie",
    title: "O Som do Mar",
    overview: "Uma viagem de reencontro pela costa. Drama fictício.",
    category: "Drama",
    durationMinutes: 116,
    badge: "2025 • 10 anos",
    playable: true,
  },
  {
    id: "series-horizonte",
    kind: "series",
    title: "Horizonte Azul",
    overview:
      "Uma equipe explora ilhas imaginárias. Série demonstrativa: temporada 1, episódio 1.",
    category: "Documentários",
    durationMinutes: 26,
    badge: "1 temporada • Livre",
    playable: true,
  },
  {
    id: "series-bairro",
    kind: "series",
    title: "Nosso Bairro",
    overview: "Pequenas histórias de uma comunidade inventada.",
    category: "Comédia",
    durationMinutes: 24,
    badge: "1 temporada • 10 anos",
    playable: true,
  },
  {
    id: "channel-aurora",
    kind: "channel",
    title: "Aurora TV",
    overview:
      "Canal fictício. Reproduz um sinal de teste próprio em loop; não é uma transmissão comercial.",
    category: "FAST",
    durationMinutes: 0,
    badge: "DEMO • Ao vivo simulado",
    playable: true,
  },
  {
    id: "channel-mundo",
    kind: "channel",
    title: "Mundo em Foco",
    overview: "Programação fictícia de documentários. Sinal de teste próprio.",
    category: "Documentários",
    durationMinutes: 0,
    badge: "DEMO • Ao vivo simulado",
    playable: true,
  },
  {
    id: "youtube-studio",
    kind: "youtube",
    title: "Estúdio IA TV",
    overview:
      "Exemplo de integração futura com a API oficial do YouTube. Vídeo não disponível neste milestone.",
    category: "Criatividade",
    durationMinutes: 8,
    badge: "MOCK • Sem reprodução",
    playable: false,
  },
];
export interface MovieProvider {
  getMovies(): Promise<ContentCard[]>;
}
export interface SeriesProvider {
  getSeries(): Promise<ContentCard[]>;
}
export interface LiveTvProvider {
  getCategories(): Promise<string[]>;
  getChannels(categoryId?: string): Promise<ContentCard[]>;
  getChannel(id: string): Promise<ContentCard | undefined>;
  getEpg(id: string): Promise<EpgProgram[]>;
}
export interface SportsProvider {
  getEventsByDate(date: string): Promise<SportsEvent[]>;
  getLiveEvents(): Promise<SportsEvent[]>;
}
export interface YouTubeProvider {
  search(q: string): Promise<ContentCard[]>;
  getTrending(): Promise<ContentCard[]>;
}
export interface MetadataProvider {
  get(id: string): Promise<ContentCard | undefined>;
}
export const normalize = (value: string) =>
  value
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toLowerCase();
@Injectable()
export class MockMovieProvider implements MovieProvider {
  async getMovies() {
    return catalog.filter((c) => c.kind === "movie");
  }
}
@Injectable()
export class MockSeriesProvider implements SeriesProvider {
  async getSeries() {
    return catalog.filter((c) => c.kind === "series");
  }
}
@Injectable()
export class MockLiveTvProvider implements LiveTvProvider {
  async getCategories() {
    return [
      "Abertos",
      "Notícias",
      "Esportes",
      "Filmes",
      "Séries",
      "Infantil",
      "Documentários",
      "Música",
      "Internacional",
      "FAST",
      "Favoritos",
    ];
  }
  async getChannels(categoryId?: string) {
    return catalog.filter(
      (c) => c.kind === "channel" && (!categoryId || c.category === categoryId),
    );
  }
  async getChannel(id: string) {
    return catalog.find((c) => c.kind === "channel" && c.id === id);
  }
  async getEpg(id: string): Promise<EpgProgram[]> {
    if (!(await this.getChannel(id))) return [];
    const start = new Date();
    start.setUTCMinutes(0, 0, 0);
    return Array.from({ length: 24 }, (_, i) => ({
      id: `${id}-${start.toISOString()}-${i}`,
      channelId: id,
      title: i === 0 ? "Paisagens imaginárias" : "Janelas do mundo",
      description: "Programação fictícia para demonstração do guia.",
      startAt: new Date(+start + i * 3600000).toISOString(),
      endAt: new Date(+start + (i + 1) * 3600000).toISOString(),
      category: "Documentários",
      imageUrl: null,
      rating: "Livre",
      season: null,
      episode: null,
      isLive: i === 0,
    }));
  }
}
export function today() {
  return new Intl.DateTimeFormat("en-CA", {
    timeZone: "America/Sao_Paulo",
  }).format(new Date());
}
@Injectable()
export class MockSportsProvider implements SportsProvider {
  async getEventsByDate(date: string): Promise<SportsEvent[]> {
    return [
      ["Aurora FC", "Estrela Azul"],
      ["Vale Unido", "Serra Clube"],
      ["Atlético Solar", "Oceano FC"],
      ["Lagoa SC", "Vila Nova Demo"],
      ["Real Horizonte", "União do Sul"],
    ].map(([homeTeam, awayTeam], i) => ({
      id: `game-${date}-${i}`,
      sport: "Futebol",
      competition: "Copa dos Horizontes • fictícia",
      round: "Rodada 1",
      homeTeam,
      awayTeam,
      homeScore: null,
      awayScore: null,
      startAt: new Date(
        `${date}T${String(17 + i).padStart(2, "0")}:00:00-03:00`,
      ).toISOString(),
      status: "SCHEDULED",
      stadium: "Arena demonstrativa",
      broadcasts: [],
    }));
  }
  async getLiveEvents() {
    return [];
  }
}
@Injectable()
export class MockYouTubeProvider implements YouTubeProvider {
  async getTrending() {
    return catalog.filter((c) => c.kind === "youtube");
  }
  async search(q: string) {
    return (await this.getTrending()).filter((c) =>
      normalize(c.title).includes(normalize(q)),
    );
  }
}
export function sportsCards(events: SportsEvent[]): ContentCard[] {
  return events.map((e) => ({
    id: e.id,
    kind: "sports",
    title: `${e.homeTeam} × ${e.awayTeam}`,
    overview: `${new Intl.DateTimeFormat("pt-BR", { timeZone: "America/Sao_Paulo", hour: "2-digit", minute: "2-digit" }).format(new Date(e.startAt))} • ${e.competition}. Nenhuma transmissão cadastrada.`,
    category: "Futebol",
    durationMinutes: 0,
    badge: "Agenda fictícia",
    playable: false,
  }));
}
export const kinds: ContentKind[] = [
  "movie",
  "series",
  "channel",
  "sports",
  "youtube",
];
