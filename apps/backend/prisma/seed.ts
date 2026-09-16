import { PrismaClient } from "@prisma/client";
import { catalog } from "../src/providers/catalog";
const db = new PrismaClient();
async function seed() {
  await db.$transaction(
    catalog.map((item) =>
      db.content.upsert({ where: { id: item.id }, create: item, update: item }),
    ),
  );
  for (const key of [
    "ai_assistant_enabled",
    "sports_enabled",
    "youtube_enabled",
    "subscriptions_enabled",
    "voice_enabled",
    "kids_profile_enabled",
  ]) {
    await db.featureFlag.upsert({
      where: { key },
      create: {
        key,
        enabled: [
          "ai_assistant_enabled",
          "sports_enabled",
          "youtube_enabled",
        ].includes(key),
      },
      update: {},
    });
  }
  console.log(`Seed concluído: ${catalog.length} conteúdos fictícios.`);
}
seed()
  .catch(() => {
    console.error("Seed falhou. Verifique o banco e as migrations.");
    process.exitCode = 1;
  })
  .finally(() => db.$disconnect());
