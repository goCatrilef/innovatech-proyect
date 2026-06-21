import { useEffect, useState } from "react";
import keycloak from "./auth/keycloak";
import { bffApi } from "./api/bffApi";

type ProyectoResumen = {
  proyecto: {
    id: number;
    codigo: string;
    nombre: string;
    estado: string;
  };
  miembros: unknown[];
  tareas: unknown[];
  totalTareas: number;
  tareasPendientes: number;
  tareasEnProgreso: number;
  tareasFinalizadas: number;
};

function App() {
  const [autenticado, setAutenticado] = useState(false);
  const [resumen, setResumen] = useState<ProyectoResumen | null>(null);
  const [error, setError] = useState("");

  useEffect(() => {
    keycloak
      .init({
        onLoad: "login-required",
        checkLoginIframe: false,
      })
      .then((authenticated) => {
        setAutenticado(authenticated);
      })
      .catch(() => {
        setError("No fue posible iniciar sesión con Keycloak");
      });
  }, []);

  const cargarResumen = async () => {
    try {
      setError("");
      const response = await bffApi.get<ProyectoResumen>(
        "/api/bff/proyectos/1/resumen"
      );
      setResumen(response.data);
    } catch {
      setError("No fue posible obtener el resumen del proyecto");
    }
  };

  const cerrarSesion = () => {
    keycloak.logout({
      redirectUri: "http://localhost:5173",
    });
  };

  if (error) {
    return (
      <main className="min-h-screen flex items-center justify-center">
        <div className="bg-white p-6 rounded-xl shadow">
          <p className="text-red-600 font-semibold">{error}</p>
        </div>
      </main>
    );
  }

  if (!autenticado) {
    return (
      <main className="min-h-screen flex items-center justify-center">
        <p className="text-slate-600">Cargando autenticación...</p>
      </main>
    );
  }

  return (
    <main className="min-h-screen p-8">
      <section className="max-w-4xl mx-auto bg-white rounded-2xl shadow p-8">
        <div className="flex justify-between items-center mb-6">
          <div>
            <h1 className="text-3xl font-bold text-slate-900">
              Innovatech Dashboard
            </h1>
            <p className="text-slate-500">
              Usuario autenticado con Keycloak
            </p>
          </div>

          <button
            onClick={cerrarSesion}
            className="px-4 py-2 rounded-lg bg-slate-900 text-white"
          >
            Cerrar sesión
          </button>
        </div>

        <button
          onClick={cargarResumen}
          className="px-5 py-3 rounded-lg bg-blue-600 text-white font-semibold"
        >
          Cargar resumen del proyecto
        </button>

        {resumen && (
          <div className="mt-8 border rounded-xl p-6">
            <h2 className="text-xl font-bold mb-2">
              {resumen.proyecto.nombre}
            </h2>

            <p className="text-slate-600">
              Código: {resumen.proyecto.codigo}
            </p>

            <p className="text-slate-600">
              Estado: {resumen.proyecto.estado}
            </p>

            <div className="grid grid-cols-4 gap-4 mt-6">
              <div className="bg-slate-100 p-4 rounded-lg">
                <p className="text-sm text-slate-500">Total tareas</p>
                <p className="text-2xl font-bold">{resumen.totalTareas}</p>
              </div>

              <div className="bg-slate-100 p-4 rounded-lg">
                <p className="text-sm text-slate-500">Pendientes</p>
                <p className="text-2xl font-bold">
                  {resumen.tareasPendientes}
                </p>
              </div>

              <div className="bg-slate-100 p-4 rounded-lg">
                <p className="text-sm text-slate-500">En progreso</p>
                <p className="text-2xl font-bold">
                  {resumen.tareasEnProgreso}
                </p>
              </div>

              <div className="bg-slate-100 p-4 rounded-lg">
                <p className="text-sm text-slate-500">Finalizadas</p>
                <p className="text-2xl font-bold">
                  {resumen.tareasFinalizadas}
                </p>
              </div>
            </div>
          </div>
        )}
      </section>
    </main>
  );
}

export default App;