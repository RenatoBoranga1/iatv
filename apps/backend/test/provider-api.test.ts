import "reflect-metadata";
import { test } from "node:test";
import assert from "node:assert/strict";
import { Test } from "@nestjs/testing";
import request from "supertest";
import { AppModule } from "../dist/app.module";
import { SafeErrorFilter, requestLog } from "../dist/http-boundary";
process.env.DATA_MODE = "mock";
process.env.CONTENT_PROVIDER = "brasiltv";
test("provider mode rejects fixture fallback and admin diagnostics require authentication", async () => {
  const mod = await Test.createTestingModule({ imports: [AppModule] }).compile();
  const app = mod.createNestApplication();
  app.use(requestLog);
  app.useGlobalFilters(new SafeErrorFilter());
  await app.init();
  try {
    for (const path of ["home", "catalog", "catalog/movie-orbita", "search?q=filme", "live/categories", "live/channel-aurora/epg", "playback/movie-orbita"]) {
      const r = await request(app.getHttpServer()).get(`/v1/${path}`).expect(503);
      assert.ok(["PROVIDER_UNAVAILABLE", "PLAYBACK_SOURCE_UNAVAILABLE"].includes(r.body.code));
      assert.equal(r.body.url, undefined);
    }
    await request(app.getHttpServer()).get("/v1/admin/providers/brasiltv/health").expect(401);
    process.env.PROVIDER_ADMIN_TOKEN = "test-only-administrative-credential-123456";
    await request(app.getHttpServer()).get("/v1/admin/providers/brasiltv/health").set("Authorization", "Bearer invalid").expect(401);
    const result = await request(app.getHttpServer()).get("/v1/admin/providers/brasiltv/health").set("Authorization", `Bearer ${process.env.PROVIDER_ADMIN_TOKEN}`).expect(200);
    assert.equal(result.headers["cache-control"], "no-store");
    assert.equal(result.body.configured, false);
    assert.equal(result.body.lastSuccess, null);
    assert.ok(!JSON.stringify(result.body).includes(process.env.PROVIDER_ADMIN_TOKEN));
  } finally { delete process.env.PROVIDER_ADMIN_TOKEN; await app.close(); }
});
