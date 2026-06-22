import axios from "axios";

type ApiErrorBody = {
  message?: string;
  mensaje?: string;
  error?: string;
};

export function getApiErrorMessage(
  error: unknown,
  fallback = "Ocurrio un error al comunicarse con el servidor."
) {
  if (!axios.isAxiosError<ApiErrorBody>(error)) {
    return error instanceof Error ? error.message : fallback;
  }

  return (
    error.response?.data?.message ??
    error.response?.data?.mensaje ??
    error.response?.data?.error ??
    fallback
  );
}

export function getApiErrorStatus(error: unknown) {
  if (!axios.isAxiosError(error)) {
    return undefined;
  }

  return error.response?.status;
}
