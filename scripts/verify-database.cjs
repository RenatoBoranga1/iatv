require("reflect-metadata");
const assert = require("node:assert/strict");
const { NestFactory } = require("@nestjs/core");
const request = require("supertest");
const { AppModule } = require("../apps/backend/dist/app.module");
if (!process.env.DATABASE_URL)
  throw new Error(
    "Defina DATABASE_URL para um banco de teste com migration e seed aplicados.",
  );
process.env.DATA_MODE = "postgres";
(async () => {
  const app = await NestFactory.create(AppModule, { logger: false });
  try {
    await app.init();
    const health = await request(app.getHttpServer())
      .get("/v1/health")
      .expect(200);
    assert.equal(health.body.dataMode, "postgres");
    const home = await request(app.getHttpServer()).get("/v1/home").expect(200);
    assert.ok(
      home.body.sections
        .find((section) => section.id === "movies")
        .items.some((item) => item.id === "movie-orbita"),
    );
    const config = await request(app.getHttpServer())
      .get("/v1/config")
      .expect(200);
    assert.equal(config.body.ai_assistant_enabled, true);
    console.log("PASS: API lê catálogo e flags persistidos no PostgreSQL.");
  } finally {
    await app.close();
  }
})().catch(() => {
  console.error(
    "Integração PostgreSQL falhou. Verifique conexão, migration e seed.",
  );
  process.exitCode = 1;
});
