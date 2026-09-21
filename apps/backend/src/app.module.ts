import { Module } from "@nestjs/common";
import { APP_GUARD } from "@nestjs/core";
import { ThrottlerGuard, ThrottlerModule } from "@nestjs/throttler";
import { Database } from "./database";
import { CatalogService } from "./catalog.service";
import { AuthorizedTools, MockAiProvider } from "./ai.service";
import { MockLiveTvProvider, MockSportsProvider } from "./providers/catalog";
import { ApiController } from "./controller";
import { ContentProviderFactory } from "./providers/content-provider.factory";
import { ProviderAdminController } from "./providers/provider-admin.controller";
@Module({
  imports: [ThrottlerModule.forRoot([{ ttl: 60000, limit: 120 }])],
  controllers: [ApiController, ProviderAdminController],
  providers: [
    Database,
    ContentProviderFactory,
    CatalogService,
    AuthorizedTools,
    MockAiProvider,
    MockLiveTvProvider,
    MockSportsProvider,
    { provide: APP_GUARD, useClass: ThrottlerGuard },
  ],
})
export class AppModule {}
