import React from "react";
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { apiError } from "../../app/api.js";
import { useAuth } from "./AuthContext.jsx";
import AuthShell from "./AuthShell.jsx";

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [values, setValues] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  function update(field, value) {
    setValues((old) => ({ ...old, [field]: value }));
  }

  async function submit(e) {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await login(values);
      navigate("/dashboard");
    } catch (err) {
      setError(apiError(err));
    } finally {
      setLoading(false);
    }
  }

  return (
    <AuthShell title="Welcome back" subtitle="Log in to track your medication expenses">
      <form onSubmit={submit} className="space-y-4">
        {error && <div className="rounded-xl bg-red-50 p-3 text-sm text-red-700">{error}</div>}
        <div>
          <label className="label">Email address</label>
          <input className="input" type="email" value={values.email} onChange={(e) => update("email", e.target.value)} required />
        </div>
        <div>
          <label className="label">Password</label>
          <input className="input" type="password" value={values.password} onChange={(e) => update("password", e.target.value)} required />
        </div>
        <button className="btn-primary w-full" disabled={loading}>{loading ? "Logging in..." : "Login"}</button>
        <p className="text-center text-sm text-slate-500">
          Don&apos;t have an account? <Link to="/register" className="font-semibold text-medify-600 hover:underline">Register here</Link>
        </p>
      </form>
    </AuthShell>
  );
}
