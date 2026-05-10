import React, { createContext, useContext, useEffect, useMemo, useState } from "react";
import api, { unwrap } from "../../app/api.js";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem("medify_token"));
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem("medify_user");
    return stored ? JSON.parse(stored) : null;
  });

  function saveSession(authData) {
    localStorage.setItem("medify_token", authData.accessToken);
    localStorage.setItem("medify_user", JSON.stringify(authData.user));
    setToken(authData.accessToken);
    setUser(authData.user);
  }

  async function login(values) {
    const data = unwrap(await api.post("/auth/login", values));
    saveSession(data);
    return data;
  }

  async function register(values) {
    return unwrap(await api.post("/auth/register", values));
  }

  async function verifyEmail(verificationToken) {
    const data = unwrap(await api.get(`/auth/verify-email?token=${encodeURIComponent(verificationToken)}`));
    saveSession(data);
    return data;
  }

  async function refreshProfile() {
    const data = unwrap(await api.get("/users/profile"));
    localStorage.setItem("medify_user", JSON.stringify(data));
    setUser(data);
    return data;
  }

  function logout() {
    api.post("/auth/logout").catch(() => {});
    localStorage.removeItem("medify_token");
    localStorage.removeItem("medify_user");
    setToken(null);
    setUser(null);
  }

  const value = useMemo(() => ({ token, user, login, register, verifyEmail, refreshProfile, logout }), [token, user]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used inside AuthProvider");
  return ctx;
}
