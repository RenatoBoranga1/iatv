import { Inject, Injectable } from "@nestjs/common";
import { CatalogService } from "../catalog.service";
import { MockLiveTvProvider } from "./catalog";
import { ContentProvider } from "./content-provider.interface";
import { MockContentProvider } from "./mock-content.provider";
import { BrasilTvProvider } from "./brasiltv/brasil-tv.provider";
@Injectable()
export class ContentProviderFactory {
  readonly provider: ContentProvider;
  readonly name: "mock" | "brasiltv";
  constructor(@Inject(CatalogService) catalog: CatalogService, @Inject(MockLiveTvProvider) live: MockLiveTvProvider) {
    const selected = process.env.CONTENT_PROVIDER || "mock";
    if (selected !== "mock" && selected !== "brasiltv") throw new Error("CONTENT_PROVIDER inválido.");
    this.name = selected;
    this.provider = selected === "mock" ? new MockContentProvider(catalog, live) : new BrasilTvProvider();
  }
  async requireCatalogContract() {
    // Until mapping is implemented, do not serve fixture catalog as provider data.
    if (this.name === "brasiltv") await this.provider.getLiveChannels({});
  }
}
