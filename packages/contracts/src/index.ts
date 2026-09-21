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
  live: boolean;
  protocol: "HLS" | "DASH" | "MPEG_TS" | "MP4" | "LOCAL_GATEWAY";
  variants: PlaybackVariant[];
  expiresAt?: string;
  contentId: string;
  url: string;
  mimeType: string;
  isLive: boolean;
  demo: boolean;
}
export interface PlaybackVariant {
  id: string;
  url: string;
  priority: number;
  quality?: string;
  width?: number;
  height?: number;
  bitrate?: number;
  headers?: Record<string, string>;
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

export interface ApiError {
  code: string;
  message: string;
  requestId: string;
}
export interface WatchProgress {
  positionMs: number;
  durationMs: number;
  percentage: number;
  updatedAt: string;
  completed: boolean;
}
/** Design contract only; no remote control endpoint exists in M2. */
export type RemoteCommand =
  | { type: "UP" | "DOWN" | "LEFT" | "RIGHT" | "SELECT" | "BACK" | "PLAY" | "PAUSE" }
  | { type: "SEEK"; positionMs: number }
  | { type: "OPEN_CONTENT" | "OPEN_CHANNEL"; contentId: string }
  | { type: "SEARCH"; query: string }
  | { type: "AI_COMMAND"; text: string };
export interface RemoteEnvelope {
  protocolVersion: 1;
  commandId: string;
  sessionId: string;
  sequence: number;
  issuedAt: string;
  expiresAt: string;
  command: RemoteCommand;
}
