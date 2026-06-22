import { NavLink, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "../../auth/AuthProvider";

type NavigationItem = {
  label: string;
  path: string;
  marker: string;
};

const navigationItems: NavigationItem[] = [
  { label: "Dashboard", path: "/dashboard", marker: "D" },
  { label: "Proyectos", path: "/projects", marker: "P" },
  { label: "Tareas", path: "/tasks", marker: "T" },
  { label: "Equipo", path: "/team", marker: "E" },
];

const pageTitles: Record<string, { title: string; description: string }> = {
  "/dashboard": {
    title: "Overview",
    description: "Resumen general del avance de tus proyectos.",
  },
  "/projects": {
    title: "Proyectos",
    description: "Gestiona las iniciativas activas de Innovatech.",
  },
  "/tasks": {
    title: "Tareas",
    description: "Visualiza y organiza el trabajo del equipo.",
  },
  "/team": {
    title: "Equipo",
    description: "Consulta miembros y su participación en proyectos.",
  },
};

function getPageInfo(pathname: string) {
  return pageTitles[pathname] ?? pageTitles["/dashboard"];
}

export function MainLayout() {
  const { logout } = useAuth();
  const location = useLocation();
  const pageInfo = getPageInfo(location.pathname);

  return (
    <div className="min-h-screen bg-slate-100 p-4 text-slate-950 lg:p-6">
      <div className="mx-auto flex min-h-[calc(100vh-2rem)] max-w-7xl overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm lg:min-h-[calc(100vh-3rem)]">
        <aside className="hidden w-64 shrink-0 border-r border-slate-200 bg-white md:flex md:flex-col">
          <div className="flex items-center gap-3 border-b border-slate-100 px-6 py-5">
            <div className="flex size-9 items-center justify-center rounded-lg bg-slate-950 text-sm font-bold text-white">
              I
            </div>
            <div>
              <p className="text-lg font-bold leading-tight">Innovatech</p>
              <p className="text-xs font-semibold uppercase tracking-wider text-slate-500">
                Solutions
              </p>
            </div>
          </div>

          <nav className="flex flex-1 flex-col gap-2 px-4 py-6">
            {navigationItems.map((item) => (
              <NavLink
                key={item.path}
                to={item.path}
                className={({ isActive }) =>
                  [
                    "flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-semibold transition",
                    isActive
                      ? "bg-cyan-400 text-slate-950"
                      : "text-slate-600 hover:bg-slate-100 hover:text-slate-950",
                  ].join(" ")
                }
              >
                <span className="flex size-7 items-center justify-center rounded-md border border-current text-xs">
                  {item.marker}
                </span>
                {item.label}
              </NavLink>
            ))}
          </nav>

          <div className="border-t border-slate-100 p-4">
            <button
              type="button"
              onClick={logout}
              className="w-full rounded-lg border border-slate-200 px-3 py-2 text-sm font-semibold text-slate-600 transition hover:border-slate-300 hover:bg-slate-50 hover:text-slate-950"
            >
              Cerrar sesión
            </button>
          </div>
        </aside>

        <div className="flex min-w-0 flex-1 flex-col">
          <header className="border-b border-slate-200 bg-white px-5 py-4 lg:px-8">
            <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
              <div>
                <h1 className="text-3xl font-bold tracking-tight">
                  {pageInfo.title}
                </h1>
                <p className="mt-1 text-sm text-slate-500">
                  {pageInfo.description}
                </p>
              </div>

              <div className="flex items-center gap-3">
                <label className="sr-only" htmlFor="global-search">
                  Buscar
                </label>
                <input
                  id="global-search"
                  type="search"
                  placeholder="Buscar..."
                  className="h-10 w-full rounded-lg border border-slate-200 bg-white px-3 text-sm outline-none transition placeholder:text-slate-400 focus:border-cyan-400 focus:ring-4 focus:ring-cyan-100 sm:w-64"
                />
                <div className="flex size-10 shrink-0 items-center justify-center rounded-full bg-slate-950 text-sm font-bold text-white">
                  IN
                </div>
              </div>
            </div>

            <nav className="mt-4 flex gap-2 overflow-x-auto md:hidden">
              {navigationItems.map((item) => (
                <NavLink
                  key={item.path}
                  to={item.path}
                  className={({ isActive }) =>
                    [
                      "whitespace-nowrap rounded-lg px-3 py-2 text-sm font-semibold transition",
                      isActive
                        ? "bg-cyan-400 text-slate-950"
                        : "bg-slate-100 text-slate-600",
                    ].join(" ")
                  }
                >
                  {item.label}
                </NavLink>
              ))}
            </nav>
          </header>

          <main className="flex-1 overflow-auto bg-slate-50 p-5 lg:p-8">
            <Outlet />
          </main>
        </div>
      </div>
    </div>
  );
}
