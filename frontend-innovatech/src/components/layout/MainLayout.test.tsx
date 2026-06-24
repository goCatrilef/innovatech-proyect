import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { MainLayout } from "./MainLayout";

const logoutMock = vi.hoisted(() => vi.fn());

vi.mock("../../auth/AuthProvider", () => ({
  useAuth: () => ({
    logout: logoutMock,
  }),
}));

function renderMainLayout(path = "/projects") {
  return render(
    <MemoryRouter initialEntries={[path]}>
      <Routes>
        <Route path="/" element={<MainLayout />}>
          <Route
            path="projects"
            element={<p>Contenido de proyectos protegido</p>}
          />
          <Route path="team" element={<p>Contenido de equipo protegido</p>} />
        </Route>
      </Routes>
    </MemoryRouter>
  );
}

describe("MainLayout", () => {
  beforeEach(() => {
    logoutMock.mockClear();
  });

  it("renders the project page title, navigation and outlet content", () => {
    renderMainLayout("/projects");

    expect(
      screen.getByRole("heading", { name: "Proyectos" })
    ).toBeInTheDocument();
    expect(
      screen.getByText("Gestiona las iniciativas activas de Innovatech.")
    ).toBeInTheDocument();
    expect(screen.getAllByRole("link", { name: /Dashboard/i })).not.toHaveLength(
      0
    );
    expect(screen.getAllByRole("link", { name: /Equipo/i })).not.toHaveLength(
      0
    );
    expect(
      screen.getByText("Contenido de proyectos protegido")
    ).toBeInTheDocument();
  });

  it("calls logout when the logout button is clicked", async () => {
    const user = userEvent.setup();
    renderMainLayout("/team");

    await user.click(screen.getByRole("button", { name: /Cerrar sesi/i }));

    expect(logoutMock).toHaveBeenCalledTimes(1);
  });
});
