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
} from "class-validator";
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
  @IsOptional() @IsDateString({ strict: true }) date?: string;
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
    if (process.env.DATA_MODE !== "mock") await this.db.$queryRaw`SELECT 1`;
    return {
      status: "ok",
      dataMode: process.env.DATA_MODE || "postgres",
      mockContent: true,
    };
  }
  @Get("home") home() {
    return this.catalog.home();
  }
  @Get("catalog") all() {
    return this.catalog.all();
  }
  @Get("catalog/:id") get(@Param("id") id: string) {
    return this.catalog.get(id);
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
  @Get("live/:id/epg") async epg(@Param("id") id: string) {
    await this.catalog.get(id);
    return this.live.getEpg(id);
  }
  @Post("ai/chat") chat(@Body() dto: ChatDto) {
    return this.ai.chat(dto.message);
  }
  @Get("playback/:id") async playback(@Param("id") id: string) {
    const item = await this.catalog.get(id);
    if (!item.playable)
      throw new ServiceUnavailableException(
        "Este conteúdo não possui transmissão autorizada.",
      );
    if (!process.env.DEMO_MEDIA_URL)
      throw new ServiceUnavailableException(
        "Sinal de teste indisponível. Gere a mídia própria conforme o README.",
      );
    return {
      contentId: id,
      url: process.env.DEMO_MEDIA_URL,
      mimeType: "video/mp4",
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
    };
  }
}
