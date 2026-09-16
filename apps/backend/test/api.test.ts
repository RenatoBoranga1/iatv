import "reflect-metadata";
import { after, before, test } from "node:test";
import assert from "node:assert/strict";
import { Test } from "@nestjs/testing";
import { INestApplication, ValidationPipe } from "@nestjs/common";
import request from "supertest";
import { AppModule } from "../dist/app.module";
process.env.DATA_MODE = "mock";
let app: INestApplication;
before(async () => {
  const mod = await Test.createTestingModule({
    imports: [AppModule],
  }).compile();
  app = mod.createNestApplication();
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
