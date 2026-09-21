import "reflect-metadata";
import { test } from "node:test";
import assert from "node:assert/strict";
import { BrasilTvProvider } from "../dist/providers/brasiltv/brasil-tv.provider";
import { ProviderError } from "../dist/providers/brasiltv/brasil-tv.errors";
import { safeShape } from "../dist/providers/trace";
import { requireProviderAdmin } from "../dist/providers/provider-admin.controller";
test("Brasil TV never returns mock success for unobserved contracts", async () => {
  const provider = new BrasilTvProvider();
  for (const method of ["authenticate", "getLiveCategories", "getLiveChannels", "getMovies", "getSeries", "getSeasons", "getEpisodes", "getEpg", "getPlayback"] as const) {
    await assert.rejects(provider[method](), ProviderError);
  }
  assert.equal(provider.health().configured, false);
  assert.equal(provider.health().lastSuccess, null);
});
test("shape recording removes nested credentials, signed paths and arbitrary values", () => {
  const input = { password: "sample-credential", nested: [{ authorization: "sample-header", url: "https://example.test/private-path?signature=sample-signature" }], session: { token: "sample-nested" } };
  const result = JSON.stringify(safeShape(input));
  for (const value of ["sample-credential", "sample-header", "private-path", "sample-signature", "sample-nested"]) assert.ok(!result.includes(value));
  assert.ok(result.includes("REDACTED"));
});
test("admin access fails closed and accepts only the configured credential", () => {
  const previous = process.env.PROVIDER_ADMIN_TOKEN;
  try {
    delete process.env.PROVIDER_ADMIN_TOKEN;
    assert.throws(() => requireProviderAdmin());
    process.env.PROVIDER_ADMIN_TOKEN = "test-only-operations-credential-123456789";
    assert.throws(() => requireProviderAdmin("Bearer wrong"));
    assert.doesNotThrow(() => requireProviderAdmin(`Bearer ${process.env.PROVIDER_ADMIN_TOKEN}`));
  } finally {
    if (previous === undefined) delete process.env.PROVIDER_ADMIN_TOKEN;
    else process.env.PROVIDER_ADMIN_TOKEN = previous;
  }
});
