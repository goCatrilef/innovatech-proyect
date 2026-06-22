import axios from "axios";
import { describe, expect, it } from "vitest";
import { getApiErrorMessage, getApiErrorStatus } from "./apiError";

describe("apiError", () => {
  it("returns the message sent by the backend", () => {
    const error = new axios.AxiosError(
      "Request failed",
      "400",
      undefined,
      undefined,
      {
        config: {},
        data: { mensaje: "El proyecto ya existe." },
        headers: {},
        status: 409,
        statusText: "Conflict",
      }
    );

    expect(getApiErrorMessage(error)).toBe("El proyecto ya existe.");
    expect(getApiErrorStatus(error)).toBe(409);
  });

  it("returns standard error messages from client-side failures", () => {
    const error = new Error("La sesion expiro.");

    expect(getApiErrorMessage(error)).toBe("La sesion expiro.");
    expect(getApiErrorStatus(error)).toBeUndefined();
  });
});
