import { ServiceUnavailableException } from "@nestjs/common";
import { PlaybackSource } from "@iatv/contracts";
import { CatalogService } from "../catalog.service";
import { MockLiveTvProvider } from "./catalog";
import { ContentProvider, ProviderContext, ProviderSession } from "./content-provider.interface";
export class MockContentProvider implements ContentProvider {
  constructor(private catalog: CatalogService, private live: MockLiveTvProvider) {}
  async authenticate(): Promise<ProviderSession> { return { provider: "mock" }; }
  async getLiveCategories() { return (await this.live.getCategories()).map(name => ({ id: name, name })); }
  async getLiveChannels(_context: ProviderContext, categoryId?: string) {
    return (await this.catalog.all()).filter(c => c.kind === "channel" && (!categoryId || c.category === categoryId));
  }
  async getEpg(_context: ProviderContext, channelId: string) {
    await this.catalog.get(channelId);
    return this.live.getEpg(channelId);
  }
  async getMovies() { return (await this.catalog.all()).filter(c => c.kind === "movie"); }
  async getSeries() { return (await this.catalog.all()).filter(c => c.kind === "series"); }
  async getSeasons() { return []; }
  async getEpisodes() { return []; }
  async getPlayback(_context: ProviderContext, id: string): Promise<PlaybackSource> {
    const item = await this.catalog.get(id);
    if (!item.playable) throw new ServiceUnavailableException();
    const url = (item.kind === "channel" ? process.env.DEMO_LIVE_URL : undefined) || process.env.DEMO_MEDIA_URL;
    if (!url) throw new ServiceUnavailableException();
    const parsed = new URL(url);
    if (!["http:", "https:"].includes(parsed.protocol) || parsed.username || parsed.password) throw new ServiceUnavailableException();
    const protocol = parsed.pathname.endsWith(".m3u8") ? "HLS" : parsed.pathname.endsWith(".mpd") ? "DASH" : "MP4";
    return {
      contentId: id, url, mimeType: protocol === "HLS" ? "application/x-mpegURL" : protocol === "DASH" ? "application/dash+xml" : "video/mp4",
      isLive: item.kind === "channel", live: item.kind === "channel", demo: true, protocol,
      variants: [{ id: "demo", url, priority: 0 }],
    };
  }
}

