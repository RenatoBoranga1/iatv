import {
  Body,
  Controller,
  Get,
  Inject,
  Param,
  Post,
  Query,
  ServiceUnavailableException,
} from "@nestjs/common";
import { ApiProperty, ApiTags } from "@nestjs/swagger";
import {
  IsDateString,
  IsOptional,
  IsString,
  MaxLength,
  MinLength,
  Matches,
} from "class-validator";
import { Throttle } from "@nestjs/throttler";
import { CatalogService } from "./catalog.service";
import { MockAiProvider } from "./ai.service";
import {
  MockLiveTvProvider,
  MockSportsProvider,
  today,
} from "./providers/catalog";
import { Database } from "./database";
class SearchDto {
  @ApiProperty() @IsString() @MaxLength(120) q = "";
}
class ChatDto {
  @ApiProperty() @IsString() @MinLength(1) @MaxLength(500) message!: string;
}
class DateDto {
  @IsOptional() @Matches(/^\d{4}-\d{2}-\d{2}$/) @IsDateString({ strict: true }) date?: string;
}
class ContentIdDto {
  @IsString() @Matches(/^[a-z0-9][a-z0-9-]{0,79}$/) id!: string;
}
@ApiTags("IA TV • demonstração")
@Controller("v1")
export class ApiController {
  constructor(
    @Inject(CatalogService) private catalog: CatalogService,
    @Inject(MockAiProvider) private ai: MockAiProvider,
    @Inject(MockSportsProvider) private sports: MockSportsProvider,
    @Inject(MockLiveTvProvider) private live: MockLiveTvProvider,
    @Inject(Database) private db: Database,
  ) {}
  @Get("health") async health() {
    if (process.env.DATA_MODE === "mock") return { status: "ok", database: "not_configured" };
    try { await this.db.$queryRaw`SELECT 1`; } catch { throw new ServiceUnavailableException(); }
    return { status: "ok", database: "ok" };
  }
  @Get("home") home() {
    return this.catalog.home();
  }
  @Get("catalog") all() {
    return this.catalog.all();
  }
  @Get("catalog/:id") get(@Param() params: ContentIdDto) {
    return this.catalog.get(params.id);
  }
  @Get("search") search(@Query() dto: SearchDto) {
    return this.catalog.search(dto.q);
  }
  @Get("sports") events(@Query() dto: DateDto) {
    return this.sports.getEventsByDate(dto.date?.slice(0, 10) || today());
  }
  @Get("live/categories") categories() {
    return this.live.getCategories();
  }
  @Get("live/:id/epg") async epg(@Param() params: ContentIdDto) {
    await this.catalog.get(params.id);
    return this.live.getEpg(params.id);
  }
  @Throttle({ default: { limit: 30, ttl: 60000 } })
  @Post("ai/chat") chat(@Body() dto: ChatDto) {
    return this.ai.chat(dto.message);
  }
  @Get("playback/:id") async playback(@Param() params: ContentIdDto) {
    const id = params.id;
    const item = await this.catalog.get(id);
    if (!item.playable)
      throw new ServiceUnavailableException(
        "Este conteúdo não possui transmissão autorizada.",
      );
    const url = (item.kind === "channel" ? process.env.DEMO_LIVE_URL : undefined) || process.env.DEMO_MEDIA_URL;
    if (!url)
      throw new ServiceUnavailableException(
        "Sinal de teste indisponível. Gere a mídia própria conforme o README.",
      );
    return {
      contentId: id,
      url,
      mimeType: new URL(url).pathname.endsWith(".m3u8") ? "application/x-mpegURL" : new URL(url).pathname.endsWith(".mpd") ? "application/dash+xml" : "video/mp4",
      isLive: item.kind === "channel",
      demo: true,
    };
  }
  @Get("config") async config() {
    if (process.env.DATA_MODE !== "mock") {
      return Object.fromEntries(
        (await this.db.featureFlag.findMany()).map((flag) => [
          flag.key,
          flag.enabled,
        ]),
      );
    }
    return {
      ai_assistant_enabled: true,
      sports_enabled: true,
      youtube_enabled: true,
      subscriptions_enabled: false,
      voice_enabled: false,
      kids_profile_enabled: false,
      mobile_pairing_enabled: false,
      telemetry_enabled: false,
    };
  }
}
