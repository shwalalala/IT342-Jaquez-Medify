import React from "react";
import { useState } from "react";
import { Link } from "react-router-dom";
import { apiError } from "../../app/api.js";
import { useAuth } from "./AuthContext.jsx";
import AuthShell from "./AuthShell.jsx";

export default function RegisterPage() {
  const { register } = useAuth();
  const [values, setValues] = useState({ firstName: "", lastName: "", email: "", password: "", confirmPassword: "" });
  const [message, setMessage] = useState("");
  const [devLink, setDevLink] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  function update(field, value) {
    setValues((old) => ({ ...old, [field]: value }));
  }

  async function submit(e) {
    e.preventDefault();
    setError("");
    setMessage("");
    setDevLink("");
    setLoading(true);
    try {
      const data = await register(values);
      setMessage(data.message);
      if (data.devVerificationLink) setDevLink(data.devVerificationLink);
    } catch (err) {
      setError(apiError(err));
    } finally {
      setLoading(false);
    }
  }

  return (
    <AuthShell title="Create Account" subtitle="Start monitoring your medicine inventory">
      <form onSubmit={submit} className="space-y-4">
        {error && <div className="rounded-xl bg-red-50 p-3 text-sm text-red-700">{error}</div>}
        {message && <div className="rounded-xl bg-emerald-50 p-3 text-sm text-emerald-700">{message}</div>}
        {devLink && (
          <div className="rounded-xl border border-amber-200 bg-amber-50 p-3 text-sm text-amber-800">
            <p className="font-semibold">Dev verification link:</p>
            <a href={devLink} className="break-all underline">{devLink}</a>
          </div>
        )}

        <div className="grid gap-3 sm:grid-cols-2">
          <div>
            <label className="label">First name</label>
            <input className="input" value={values.firstName} onChange={(e) => update("firstName", e.target.value)} required />
          </div>
          <div>
            <label className="label">Last name</label>
            <input className="input" value={values.lastName} onChange={(e) => update("lastName", e.target.value)} />
          </div>
        </div>
        <div>
          <label className="label">Email address</label>
          <input className="input" type="email" value={values.email} onChange={(e) => update("email", e.target.value)} required />
        </div>
        <div>
          <label className="label">Password</label>
          <input className="input" type="password" value={values.password} onChange={(e) => update("password", e.target.value)} required />
          <p className="mt-1 text-xs text-slate-500">Minimum 6 characters with 1 capital letter and 1 number or symbol.</p>
        </div>
        <div>
          <label className="label">Confirm password</label>
          <input className="input" type="password" value={values.confirmPassword} onChange={(e) => update("confirmPassword", e.target.value)} required />
        </div>
        <button className="btn-primary w-full" disabled={loading}>{loading ? "Creating..." : "Register"}</button>
        <p className="text-center text-sm text-slate-500">
          Already have an account? <Link to="/login" className="font-semibold text-medify-600 hover:underline">Login here</Link>
        </p>
      </form>
    </AuthShell>
  );
}
