"use client";
import { FormEvent, useState } from "react";
type Health = { configured: boolean; auth: string; catalog: string; broker: string; playback: string; lastSuccess: string | null; lastError: string | null; latencyMs: number | null };
export default function ProviderPage() {
  const [health, setHealth] = useState<Health | null>(null);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  async function inspect(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const form = event.currentTarget;
    const credential = String(new FormData(form).get("credential") || "");
    form.reset();
    setBusy(true); setError(""); setHealth(null);
    try {
      const response = await fetch("/api/provider-health", { headers: { Authorization: `Bearer ${credential}` }, cache: "no-store" });
      if (!response.ok) throw new Error(response.status === 401 ? "Acesso administrativo necessário." : "Diagnóstico indisponível.");
      setHealth(await response.json());
    } catch (e) { setError(e instanceof Error ? e.message : "Diagnóstico indisponível."); }
    finally { setBusy(false); }
  }
  return <main><article>
    <a href="/">← Administração</a><h1>Provedores / Brasil TV</h1>
    <p>Diagnóstico restrito a operadores. A credencial não é armazenada neste navegador.</p>
    <form onSubmit={inspect}><label>Credencial administrativa <input name="credential" type="password" required minLength={32} autoComplete="off" /></label> <button disabled={busy}>{busy ? "Consultando…" : "Consultar"}</button></form>
    {error && <p role="alert">{error}</p>}
    {!health && !busy && !error && <p>Nenhum diagnóstico carregado.</p>}
    {health && <dl>{Object.entries({ Configuration: health.configured ? "Configurado" : "Pendente", Auth: health.auth, Catalog: health.catalog, Broker: health.broker, Playback: health.playback, "Last Success": health.lastSuccess ?? "Não observado", "Last Error": health.lastError ?? "Nenhum", Latency: health.latencyMs === null ? "Não medida" : `${health.latencyMs} ms` }).map(([label, value]) => <div key={label}><dt>{label}</dt><dd>{value}</dd></div>)}</dl>}
  </article></main>;
}
