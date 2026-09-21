import { ContentCard, EpgProgram, PlaybackSource } from "@iatv/contracts";

/** Internal identity, not a claim about the legacy authentication fields. */
export interface ProviderDeviceIdentity {
  manufacturer: string;
  model: string;
  osVersion: string;
  appVersion: string;
  registrationId?: string;
}
export interface ProviderContext { device?: ProviderDeviceIdentity; session?: ProviderSession }
export interface ProviderAuthContext extends ProviderContext { credentials?: { username: string; password: string } }
export interface ProviderSession { provider: "mock" | "brasiltv"; expiresAt?: string }
export interface LiveCategory { id: string; name: string }
export interface Season { id: string; seriesId: string; number: number }
export interface Episode { id: string; seasonId: string; number: number; title: string }
export interface ContentProvider {
  authenticate(context: ProviderAuthContext): Promise<ProviderSession>;
  getLiveCategories(context: ProviderContext): Promise<LiveCategory[]>;
  getLiveChannels(context: ProviderContext, categoryId?: string): Promise<ContentCard[]>;
  getEpg(context: ProviderContext, channelId: string): Promise<EpgProgram[]>;
  getMovies(context: ProviderContext): Promise<ContentCard[]>;
  getSeries(context: ProviderContext): Promise<ContentCard[]>;
  getSeasons(context: ProviderContext, seriesId: string): Promise<Season[]>;
  getEpisodes(context: ProviderContext, seasonId: string): Promise<Episode[]>;
  getPlayback(context: ProviderContext, contentId: string): Promise<PlaybackSource>;
}
