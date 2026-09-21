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
  MockSportsProvider,
  today,
} from "./providers/catalog";
import { Database } from "./database";
import { ContentProviderFactory } from "./providers/content-provider.factory";
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
    @Inject(Database) private db: Database,
    @Inject(ContentProviderFactory) private providers: ContentProviderFactory,
  ) {}
  @Get("health") async health() {
    if (process.env.DATA_MODE === "mock") return { status: "ok", database: "not_configured" };
    try { await this.db.$queryRaw`SELECT 1`; } catch { throw new ServiceUnavailableException(); }
    return { status: "ok", database: "ok" };
  }
  @Get("home") async home() {
    await this.providers.requireCatalogContract();
    return this.catalog.home();
  }
  @Get("catalog") async all() {
    await this.providers.requireCatalogContract();
    return this.catalog.all();
  }
  @Get("catalog/:id") async get(@Param() params: ContentIdDto) {
    await this.providers.requireCatalogContract();
    return this.catalog.get(params.id);
  }
  @Get("search") async search(@Query() dto: SearchDto) {
    await this.providers.requireCatalogContract();
    return this.catalog.search(dto.q);
  }
  @Get("sports") events(@Query() dto: DateDto) {
    return this.sports.getEventsByDate(dto.date?.slice(0, 10) || today());
  }
  @Get("live/categories") categories() {
    return this.providers.provider.getLiveCategories({}).then(items => items.map(item => item.name));
  }
  @Get("live/:id/epg") async epg(@Param() params: ContentIdDto) {
    return this.providers.provider.getEpg({}, params.id);
  }
  @Throttle({ default: { limit: 30, ttl: 60000 } })
  @Post("ai/chat") async chat(@Body() dto: ChatDto) {
    await this.providers.requireCatalogContract();
    return this.ai.chat(dto.message);
  }
  @Get("playback/:id") async playback(@Param() params: ContentIdDto) {
    return this.providers.provider.getPlayback({}, params.id);
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
