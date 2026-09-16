import type { ReactNode } from "react";
import "./style.css";
export const metadata = {
  title: "IA TV • Administração",
  description: "Estrutura inicial do painel IA TV",
};
export default function Layout({ children }: { children: ReactNode }) {
  return (
    <html lang="pt-BR">
      <body>{children}</body>
    </html>
  );
}
