export type ContentKind = "movie" | "series" | "channel" | "sports" | "youtube";
export interface ContentCard {
  id: string;
  kind: ContentKind;
  title: string;
  overview: string;
  category: string;
  durationMinutes: number;
  badge: string;
  playable: boolean;
}
export interface HomeSection {
  id: string;
  title: string;
  items: ContentCard[];
}
export interface HomeResponse {
  greeting: string;
  sections: HomeSection[];
  mock: true;
}
export interface PlaybackSource {
  contentId: string;
  url: string;
  mimeType: string;
  isLive: boolean;
  demo: boolean;
}
export interface EpgProgram {
  id: string;
  channelId: string;
  title: string;
  description: string;
  startAt: string;
  endAt: string;
  category: string;
  imageUrl: string | null;
  rating: string;
  season: number | null;
  episode: number | null;
  isLive: boolean;
}
export interface SportsEvent {
  id: string;
  sport: string;
  competition: string;
  round: string;
  homeTeam: string;
  awayTeam: string;
  homeScore: number | null;
  awayScore: number | null;
  startAt: string;
  status: string;
  stadium: string;
  broadcasts: { name: string; authorized: boolean; deepLink: string | null }[];
}
export enum Intent {
  SEARCH_MOVIE = "SEARCH_MOVIE",
  SEARCH_SERIES = "SEARCH_SERIES",
  SEARCH_CHANNEL = "SEARCH_CHANNEL",
  SEARCH_YOUTUBE = "SEARCH_YOUTUBE",
  SEARCH_SPORT = "SEARCH_SPORT",
  TODAY_GAMES = "TODAY_GAMES",
  LIVE_GAMES = "LIVE_GAMES",
  WHERE_TO_WATCH = "WHERE_TO_WATCH",
  GET_RECOMMENDATIONS = "GET_RECOMMENDATIONS",
  CONTINUE_WATCHING = "CONTINUE_WATCHING",
  OPEN_CONTENT = "OPEN_CONTENT",
  PLAY_CONTENT = "PLAY_CONTENT",
  ADD_FAVORITE = "ADD_FAVORITE",
  REMOVE_FAVORITE = "REMOVE_FAVORITE",
  HELP = "HELP",
  UNKNOWN = "UNKNOWN",
}
export interface AssistantResponse {
  message: string;
  intent: Intent;
  cards: ContentCard[];
  mock: boolean;
}
