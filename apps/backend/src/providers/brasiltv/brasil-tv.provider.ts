import { ContentCard, EpgProgram, PlaybackSource } from "@iatv/contracts";
import { ContentProvider, Episode, LiveCategory, ProviderSession, Season } from "../content-provider.interface";
import { ProviderError } from "./brasil-tv.errors";

/** Fail closed until observed contracts exist. Never substitutes demonstration media. */
export class BrasilTvProvider implements ContentProvider {
  private unavailable(): never { throw new ProviderError("PROVIDER_UNAVAILABLE"); }
  async authenticate(): Promise<ProviderSession> { return this.unavailable(); }
  async getLiveCategories(): Promise<LiveCategory[]> { return this.unavailable(); }
  async getLiveChannels(): Promise<ContentCard[]> { return this.unavailable(); }
  async getEpg(): Promise<EpgProgram[]> { return this.unavailable(); }
  async getMovies(): Promise<ContentCard[]> { return this.unavailable(); }
  async getSeries(): Promise<ContentCard[]> { return this.unavailable(); }
  async getSeasons(): Promise<Season[]> { return this.unavailable(); }
  async getEpisodes(): Promise<Episode[]> { return this.unavailable(); }
  async getPlayback(): Promise<PlaybackSource> { throw new ProviderError("PLAYBACK_SOURCE_UNAVAILABLE"); }
  health() {
    return {
      provider: "brasiltv", configured: false, auth: "unknown", catalog: "unknown",
      broker: "unknown", playback: "unknown", lastSuccess: null,
      lastError: "REQUIRES_PROVIDER_CONTRACT", latencyMs: null,
    };
  }
}
