import React from "react";
import { useEffect, useState } from "react";
import api, { apiError, unwrap } from "../../app/api.js";
import { useAuth } from "../auth/AuthContext.jsx";

export default function ProfilePage() {
  const { user, refreshProfile } = useAuth();
  const [profile, setProfile] = useState(user);
  const [profileForm, setProfileForm] = useState({ firstName: user?.firstName || "", lastName: user?.lastName || "" });
  const [passwordForm, setPasswordForm] = useState({ currentPassword: "", newPassword: "", confirmPassword: "" });
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    api.get("/users/profile")
      .then((response) => {
        const data = unwrap(response);
        setProfile(data);
        setProfileForm({ firstName: data.firstName || "", lastName: data.lastName || "" });
      })
      .catch((err) => setError(apiError(err)));
  }, []);

  async function updateProfile(e) {
    e.preventDefault();
    setLoading(true);
    setError("");
    setMessage("");
    try {
      const data = unwrap(await api.put("/users/profile", profileForm));
      setProfile(data);
      await refreshProfile();
      setMessage("Profile updated successfully.");
    } catch (err) {
      setError(apiError(err));
    } finally {
      setLoading(false);
    }
  }

  async function changePassword(e) {
    e.preventDefault();
    setLoading(true);
    setError("");
    setMessage("");
    try {
      await api.put("/users/profile/password", passwordForm);
      setPasswordForm({ currentPassword: "", newPassword: "", confirmPassword: "" });
      setMessage("Password changed successfully.");
    } catch (err) {
      setError(apiError(err));
    } finally {
      setLoading(false);
    }
  }

  async function uploadImage(e) {
    const file = e.target.files?.[0];
    if (!file) return;
    const data = new FormData();
    data.append("file", file);
    setError("");
    setMessage("");
    try {
      const response = await api.post("/users/profile/image", data, { headers: { "Content-Type": "multipart/form-data" } });
      const updated = unwrap(response);
      setProfile(updated);
      await refreshProfile();
      setMessage("Profile image uploaded successfully.");
    } catch (err) {
      setError(apiError(err));
    }
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-slate-900">Profile Management</h1>
        <p className="mt-1 text-sm text-slate-500">Update your personal information and security settings.</p>
      </div>

      {message && <div className="rounded-xl bg-emerald-50 p-3 text-sm text-emerald-700">{message}</div>}
      {error && <div className="rounded-xl bg-red-50 p-3 text-sm text-red-700">{error}</div>}

      <div className="grid gap-6 xl:grid-cols-[360px_1fr]">
        <section className="card">
          <h2 className="mb-4 text-lg font-bold">Profile Picture</h2>
          <div className="mb-4 grid place-items-center">
            {profile?.profileImageUrl ? (
              <img src={profile.profileImageUrl} alt="Profile" className="h-36 w-36 rounded-3xl object-cover ring-4 ring-slate-100" />
            ) : (
              <div className="grid h-36 w-36 place-items-center rounded-3xl bg-slate-100 text-4xl font-black text-slate-400">
                {profile?.firstName?.charAt(0) || "M"}
              </div>
            )}
          </div>
          <label className="btn-secondary w-full cursor-pointer">
            Upload JPG/PNG
            <input type="file" accept="image/png,image/jpeg" onChange={uploadImage} className="hidden" />
          </label>
          <p className="mt-2 text-center text-xs text-slate-500">Maximum file size: 5MB</p>
        </section>

        <section className="card">
          <h2 className="mb-4 text-lg font-bold">Personal Information</h2>
          <form onSubmit={updateProfile} className="space-y-4">
            <div className="grid gap-4 md:grid-cols-2">
              <div>
                <label className="label">First name</label>
                <input className="input" value={profileForm.firstName} onChange={(e) => setProfileForm({ ...profileForm, firstName: e.target.value })} required />
              </div>
              <div>
                <label className="label">Last name</label>
                <input className="input" value={profileForm.lastName} onChange={(e) => setProfileForm({ ...profileForm, lastName: e.target.value })} />
              </div>
            </div>
            <div>
              <label className="label">Email address</label>
              <input className="input bg-slate-50" value={profile?.email || ""} readOnly />
            </div>
            <button className="btn-primary" disabled={loading}>{loading ? "Saving..." : "Save Changes"}</button>
          </form>
        </section>
      </div>

      <section className="card max-w-3xl">
        <h2 className="mb-4 text-lg font-bold">Security Settings</h2>
        <form onSubmit={changePassword} className="space-y-4">
          <div>
            <label className="label">Current password</label>
            <input className="input" type="password" value={passwordForm.currentPassword} onChange={(e) => setPasswordForm({ ...passwordForm, currentPassword: e.target.value })} required />
          </div>
          <div className="grid gap-4 md:grid-cols-2">
            <div>
              <label className="label">New password</label>
              <input className="input" type="password" value={passwordForm.newPassword} onChange={(e) => setPasswordForm({ ...passwordForm, newPassword: e.target.value })} required />
            </div>
            <div>
              <label className="label">Confirm new password</label>
              <input className="input" type="password" value={passwordForm.confirmPassword} onChange={(e) => setPasswordForm({ ...passwordForm, confirmPassword: e.target.value })} required />
            </div>
          </div>
          <p className="text-xs text-slate-500">Password must be at least 6 characters with 1 capital letter and 1 number or symbol.</p>
          <button className="btn-primary" disabled={loading}>{loading ? "Changing..." : "Change Password"}</button>
        </form>
      </section>
    </div>
  );
}
