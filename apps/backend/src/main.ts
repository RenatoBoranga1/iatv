import "reflect-metadata";
import { ValidationPipe } from "@nestjs/common";
import { NestFactory } from "@nestjs/core";
import { DocumentBuilder, SwaggerModule } from "@nestjs/swagger";
import helmet from "helmet";
import { AppModule } from "./app.module";
import { resolve } from "node:path";
import { NestExpressApplication } from "@nestjs/platform-express";
import { SafeErrorFilter, requestLog } from "./http-boundary";
export function validateEnvironment() {
  if (!["mock", "postgres"].includes(process.env.DATA_MODE || "postgres"))
    throw new Error("DATA_MODE deve ser mock ou postgres");
  if (
    process.env.DATA_MODE !== "mock" &&
    !process.env.DATABASE_URL?.startsWith("postgresql://")
  )
    throw new Error("DATABASE_URL PostgreSQL obrigatória");
  if (process.env.NODE_ENV === "production" && process.env.DATA_MODE === "mock")
    throw new Error("Modo mock em memória restrito a desenvolvimento");
  for (const name of ["DEMO_MEDIA_URL", "DEMO_LIVE_URL"]) {
    const value = process.env[name];
    if (!value) continue;
    const url = new URL(value);
    if (!["http:", "https:"].includes(url.protocol) || url.username || url.password)
      throw new Error(`${name} deve ser HTTP(S), sem credenciais`);
  }
  const port = Number(process.env.PORT || 3000);
  if (!Number.isInteger(port) || port < 1 || port > 65535) throw new Error("PORT inválida");
}
async function bootstrap() {
  validateEnvironment();
  const app = await NestFactory.create<NestExpressApplication>(AppModule, {
    logger: ["error", "warn", "log"],
  });
  app.use(helmet());
  app.use(requestLog);
  app.useGlobalFilters(new SafeErrorFilter());
  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      forbidNonWhitelisted: true,
      transform: true,
    }),
  );
  app.useStaticAssets(resolve(__dirname, "../media"), { prefix: "/media/" });
  const document = SwaggerModule.createDocument(
    app,
    new DocumentBuilder()
      .setTitle("IA TV API")
      .setVersion("0.1.0")
      .setDescription(
        "API pública de demonstração. Catálogo fictício, sem contas ou dados pessoais.",
      )
      .build(),
  );
  SwaggerModule.setup("docs", app, document);
  app.enableShutdownHooks();
  await app.listen(Number(process.env.PORT || 3000), "0.0.0.0");
}
void bootstrap();
