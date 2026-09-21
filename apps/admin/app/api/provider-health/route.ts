export async function GET(request: Request) {
  const authorization = request.headers.get("authorization");
  const safeHeaders = { "Cache-Control": "no-store" };
  if (!authorization?.startsWith("Bearer ") || authorization.length > 1024) return Response.json({ error: "UNAUTHORIZED" }, { status: 401, headers: safeHeaders });
  try {
    // Server-only destination; neither the client nor provider can select a proxy URL.
    const base = new URL(process.env.IATV_BACKEND_URL || "http://127.0.0.1:3000");
    if (base.username || base.password || !["http:", "https:"].includes(base.protocol)) throw new Error();
    const response = await fetch(new URL("/v1/admin/providers/brasiltv/health", base), { headers: { authorization }, cache: "no-store", redirect: "error", signal: AbortSignal.timeout(5000) });
    if (!response.ok) return Response.json({ error: response.status === 401 ? "UNAUTHORIZED" : "UNAVAILABLE" }, { status: response.status === 401 ? 401 : 503, headers: safeHeaders });
    const data = await response.json();
    const state = (v: unknown) => ["ok", "unknown", "error"].includes(String(v)) ? v : "unknown";
    return Response.json({ configured: data.configured === true, auth: state(data.auth), catalog: state(data.catalog), broker: state(data.broker), playback: state(data.playback), lastSuccess: null, lastError: data.lastError === "REQUIRES_PROVIDER_CONTRACT" ? data.lastError : null, latencyMs: typeof data.latencyMs === "number" && Number.isFinite(data.latencyMs) ? data.latencyMs : null }, { headers: safeHeaders });
  } catch { return Response.json({ error: "UNAVAILABLE" }, { status: 503, headers: safeHeaders }); }
}
