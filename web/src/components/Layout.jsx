import React from "react";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../features/auth/AuthContext.jsx";

const links = [
  { to: "/dashboard", label: "Dashboard", icon: "▦" },
  { to: "/medications", label: "Medications", icon: "✚" },
  { to: "/profile", label: "Profile", icon: "◉" }
];

export default function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate("/login");
  }

  return (
    <div className="min-h-screen bg-slate-50 lg:flex">
      <aside className="border-b border-slate-200 bg-white lg:fixed lg:inset-y-0 lg:left-0 lg:w-64 lg:border-b-0 lg:border-r">
        <div className="flex items-center justify-between px-4 py-4 lg:block">
          <div className="flex items-center gap-3">
            <div className="grid h-11 w-11 place-items-center rounded-2xl bg-medify-600 text-lg font-black text-white">M</div>
            <div>
              <h1 className="font-bold leading-tight">Medify</h1>
              <p className="text-xs text-slate-500">Medication & Expense Tracker</p>
            </div>
          </div>
          <button onClick={handleLogout} className="btn-secondary lg:hidden">Logout</button>
        </div>

        <nav className="flex gap-2 overflow-x-auto px-4 pb-4 lg:block lg:space-y-2">
          {links.map((link) => (
            <NavLink
              key={link.to}
              to={link.to}
              className={({ isActive }) =>
                `flex min-h-11 shrink-0 items-center gap-3 rounded-xl px-3 py-2 text-sm font-medium transition ${
                  isActive ? "bg-medify-50 text-medify-700" : "text-slate-600 hover:bg-slate-100"
                }`
              }
            >
              <span>{link.icon}</span>
              {link.label}
            </NavLink>
          ))}
        </nav>

        <div className="hidden border-t border-slate-200 p-4 lg:absolute lg:bottom-0 lg:block lg:w-full">
          <p className="mb-3 text-xs text-slate-500">Signed in as</p>
          <p className="truncate text-sm font-semibold">{user?.firstName} {user?.lastName}</p>
          <p className="truncate text-xs text-slate-500">{user?.email}</p>
          <button onClick={handleLogout} className="btn-secondary mt-4 w-full">Logout</button>
        </div>
      </aside>

      <main className="w-full p-4 lg:ml-64 lg:p-8">
        <Outlet />
      </main>
    </div>
  );
}
