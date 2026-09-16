CREATE TABLE "Content" (
  "id" TEXT NOT NULL PRIMARY KEY,
  "kind" TEXT NOT NULL,
  "title" TEXT NOT NULL,
  "overview" TEXT NOT NULL,
  "category" TEXT NOT NULL,
  "durationMinutes" INTEGER NOT NULL,
  "badge" TEXT NOT NULL,
  "playable" BOOLEAN NOT NULL DEFAULT false
);
CREATE INDEX "Content_kind_category_idx" ON "Content"("kind", "category");
CREATE TABLE "FeatureFlag" ("key" TEXT NOT NULL PRIMARY KEY, "enabled" BOOLEAN NOT NULL DEFAULT false);
