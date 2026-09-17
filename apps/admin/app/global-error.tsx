"use client";

export default function GlobalError({ reset }: { reset: () => void }) {
  return <html lang="pt-BR"><body><h1>Painel temporariamente indisponível.</h1><button onClick={reset}>Tentar novamente</button></body></html>;
}
