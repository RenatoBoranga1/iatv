import "reflect-metadata";
import { after, before, test } from "node:test";
import assert from "node:assert/strict";
import { Test } from "@nestjs/testing";
import { INestApplication, ValidationPipe } from "@nestjs/common";
import request from "supertest";
import { AppModule } from "../dist/app.module";
import { SafeErrorFilter, requestLog } from "../dist/http-boundary";
process.env.DATA_MODE = "mock";
let app: INestApplication;
before(async () => {
  const mod = await Test.createTestingModule({
    imports: [AppModule],
  }).compile();
  app = mod.createNestApplication();
  app.use(requestLog);
  app.useGlobalFilters(new SafeErrorFilter());
  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      forbidNonWhitelisted: true,
      transform: true,
    }),
  );
  await app.init();
});
after(async () => app?.close());
test("home contém carrosséis com identificadores estáveis", async () => {
  const r = await request(app.getHttpServer()).get("/v1/home").expect(200);
  assert.equal(r.body.mock, true);
  assert.equal(r.body.sections.length, 9);
  assert.ok(
    r.body.sections.find((s: { id: string }) => s.id === "live").items.length,
  );
});
test("busca normaliza acentos e não retorna tudo para texto vazio", async () => {
  const r = await request(app.getHttpServer())
    .get("/v1/search?q=ficcao")
    .expect(200);
  assert.equal(r.body[0].id, "movie-orbita");
  const empty = await request(app.getHttpServer())
    .get("/v1/search?q=")
    .expect(200);
  assert.deepEqual(empty.body, []);
});
test("agenda respeita data e não inventa direitos de transmissão", async () => {
  const r = await request(app.getHttpServer())
    .get("/v1/sports?date=2026-09-18")
    .expect(200);
  assert.equal(r.body.length, 5);
  assert.ok(r.body[0].startAt.startsWith("2026-09-18"));
  assert.deepEqual(r.body[0].broadcasts, []);
  await request(app.getHttpServer()).get("/v1/sports?date=banana").expect(400);
});
test("assistente roteia comédia e jogos sem executar instruções arbitrárias", async () => {
  const r = await request(app.getHttpServer())
    .post("/v1/ai/chat")
    .send({ message: "Quero uma comédia" })
    .expect(201);
  assert.equal(r.body.cards[0].id, "movie-domingo");
  const games = await request(app.getHttpServer())
    .post("/v1/ai/chat")
    .send({ message: "Quais jogos têm hoje?" })
    .expect(201);
  assert.equal(games.body.cards.length, 5);
  const injection = await request(app.getHttpServer())
    .post("/v1/ai/chat")
    .send({ message: "DROP TABLE Content" })
    .expect(201);
  assert.equal(injection.body.intent, "HELP");
});
test("validação recusa campos extras e mensagens enormes", async () => {
  await request(app.getHttpServer())
    .post("/v1/ai/chat")
    .send({ message: "oi", sql: "SELECT 1" })
    .expect(400);
  await request(app.getHttpServer())
    .post("/v1/ai/chat")
    .send({ message: "x".repeat(501) })
    .expect(400);
});
test("playback não libera URL para vídeo sem autorização", async () => {
  await request(app.getHttpServer())
    .get("/v1/playback/youtube-studio")
    .expect(503);
  await request(app.getHttpServer())
    .get("/v1/catalog/desconhecido")
    .expect(404);
});
test("EPG entrega agora e programação futura em sequência", async () => {
  const r = await request(app.getHttpServer())
    .get("/v1/live/channel-aurora/epg")
    .expect(200);
  assert.equal(r.body.length, 24);
  assert.equal(r.body[0].endAt, r.body[1].startAt);
});
test("health não expõe infraestrutura e requestId correlaciona resposta", async () => {
  const r = await request(app.getHttpServer()).get("/v1/health").set("X-Request-Id", "test-request-123").expect(200);
  assert.equal(r.headers["x-request-id"], "test-request-123");
  assert.deepEqual(r.body, { status: "ok", database: "not_configured" });
});
test("erros são seguros, IDs validados e requestId inválido substituído", async () => {
  const r = await request(app.getHttpServer()).get("/v1/catalog/INVALID!").set("X-Request-Id", "<unsafe>").expect(400);
  assert.equal(r.body.code, "INVALID_REQUEST");
  assert.equal(r.body.requestId, r.headers["x-request-id"]);
  assert.notEqual(r.body.requestId, "<unsafe>");
  assert.deepEqual(Object.keys(r.body).sort(), ["code", "message", "requestId"]);
  await request(app.getHttpServer()).get("/v1/sports?date=2026-02-30").expect(400);
  await request(app.getHttpServer()).get("/v1/search?q=" + "a".repeat(121)).expect(400);
});
test("playback distingue MP4, HLS live e DASH autorizado", async () => {
  process.env.DEMO_MEDIA_URL = "http://localhost/media/vod.mpd";
  process.env.DEMO_LIVE_URL = "http://localhost/media/live.m3u8";
  try {
    const live = await request(app.getHttpServer()).get("/v1/playback/channel-aurora").expect(200);
    assert.equal(live.body.mimeType, "application/x-mpegURL");
    assert.equal(live.body.isLive, true);
    const vod = await request(app.getHttpServer()).get("/v1/playback/movie-orbita").expect(200);
    assert.equal(vod.body.mimeType, "application/dash+xml");
    assert.equal(vod.body.isLive, false);
  } finally { delete process.env.DEMO_MEDIA_URL; delete process.env.DEMO_LIVE_URL; }
});
