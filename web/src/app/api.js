import axios from "axios";

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/v1";

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json"
  }
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("medify_token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export function unwrap(response) {
  return response.data.data;
}

export function apiError(error) {
  const payload = error?.response?.data;
  if (payload?.error?.details && typeof payload.error.details === "object") {
    return Object.values(payload.error.details).join(" ");
  }
  return payload?.error?.message || error?.message || "Something went wrong";
}

export default api;
