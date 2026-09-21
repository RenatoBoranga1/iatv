const areas = [
  "Dashboard",
  "Usuários",
  "Perfis",
  "Dispositivos",
  "Conteúdo",
  "TV ao Vivo",
  "Filmes",
  "Séries",
  "Esportes",
  "Provedores",
  "Assinaturas",
  "Feature Flags",
  "Configurações",
];
export default function Page() {
  return (
    <main>
      <aside>
        <strong>IA TV</strong>
        <p>Administração</p>
        <nav aria-label="Módulos planejados">
          {areas.map((area) => (
            area === "Provedores" ? <a key={area} href="/providers/brasiltv">{area}</a> : <span key={area}>{area}</span>
          ))}
        </nav>
      </aside>
      <article>
        <small>MILESTONE 01 / FUNDAÇÃO</small>
        <h1>
          Uma nova forma
          <br />
          de reunir entretenimento.
        </h1>
        <p>
          Catálogo fictício, API própria e experiência nativa para Android TV.
        </p>
        <section>
          <h2>Ambiente de demonstração</h2>
          <p>
            Este painel é a estrutura inicial. Gestão de usuários, publicação de
            conteúdo e alterações administrativas serão habilitadas depois da
            autenticação e da auditoria.
          </p>
          <p>
            Os módulos listados estão planejados; não há operações
            administrativas disponíveis nesta versão.
          </p>
        </section>
      </article>
    </main>
  );
}
