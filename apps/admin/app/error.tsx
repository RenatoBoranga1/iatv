"use client";

export default function ErrorPage({ reset }: { reset: () => void }) {
  return <main><article><h1>Não foi possível abrir o painel.</h1><p>Tente novamente em instantes.</p><button onClick={reset}>Tentar novamente</button></article></main>;
}
