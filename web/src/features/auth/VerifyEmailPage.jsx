import React from "react";
import { useEffect, useRef, useState } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { apiError } from "../../app/api.js";
import { useAuth } from "./AuthContext.jsx";
import AuthShell from "./AuthShell.jsx";

export default function VerifyEmailPage() {
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const { verifyEmail } = useAuth();
  const [status, setStatus] = useState("Verifying your account...");
  const [error, setError] = useState("");
  const didRun = useRef(false);

  useEffect(() => {
    if (didRun.current) return;
    didRun.current = true;

    const token = params.get("token");
    if (!token) {
      setError("Missing verification token.");
      return;
    }

    verifyEmail(token)
      .then(() => {
        setStatus("Email verified. Redirecting to dashboard...");
        setTimeout(() => navigate("/dashboard"), 700);
      })
      .catch((err) => setError(apiError(err)));
  }, [params, verifyEmail, navigate]);

  return (
    <AuthShell title="Email Verification" subtitle="Finishing your Medify setup">
      {error ? (
        <div className="space-y-4">
          <div className="rounded-xl bg-red-50 p-3 text-sm text-red-700">{error}</div>
          <Link to="/login" className="btn-secondary w-full">Back to login</Link>
        </div>
      ) : (
        <div className="rounded-xl bg-medify-50 p-4 text-center text-sm font-medium text-medify-700">{status}</div>
      )}
    </AuthShell>
  );
}
