import React from "react";
import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import api, { apiError, unwrap } from "../../app/api.js";
import EmptyState from "../../components/EmptyState.jsx";
import { currency, readableDate } from "../../utils/format.js";

export default function DashboardPage() {
  const [dashboard, setDashboard] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get("/dashboard")
      .then((response) => setDashboard(unwrap(response)))
      .catch((err) => setError(apiError(err)))
      .finally(() => setLoading(false));
  }, []);

  const maxMonth = useMemo(() => {
    const totals = dashboard?.monthlySeries?.map((p) => Number(p.total)) || [0];
    return Math.max(...totals, 1);
  }, [dashboard]);

  if (loading) return <PageTitle title="Dashboard" subtitle="Loading your summary..." />;
  if (error) return <PageTitle title="Dashboard" subtitle={error} danger />;

  return (
    <div className="space-y-6">
      <PageTitle title={`Hi, ${dashboard.user.firstName}!`} subtitle="Here is your medication inventory and spending summary." />

      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        <StatCard label="Weekly expenses" value={currency(dashboard.weeklyExpense)} hint="This week" />
        <StatCard label="Monthly expenses" value={currency(dashboard.monthlyExpense)} hint="Current month" />
        <StatCard label="Current medications" value={dashboard.currentMedicationCount} hint="All records" />
        <StatCard label="Total expenses" value={currency(dashboard.allTimeExpense)} hint="All-time medication cost" />
      </div>

      <div className="grid gap-6 xl:grid-cols-2">
        <section className="card">
          <div className="mb-4 flex items-center justify-between gap-3">
            <div>
              <h2 className="text-lg font-bold">Current Medications</h2>
              <p className="text-sm text-slate-500">Latest purchase records</p>
            </div>
            <Link to="/medications" className="btn-secondary">View all</Link>
          </div>
          {dashboard.currentMedications.length === 0 ? (
            <EmptyState title="No medications yet" message="Click Medications to add your first record." />
          ) : (
            <MedicationList medications={dashboard.currentMedications} />
          )}
        </section>

        <section className="card">
          <div className="mb-4 flex items-center justify-between gap-3">
            <div>
              <h2 className="text-lg font-bold">Recently Added</h2>
              <p className="text-sm text-slate-500">Newest medication entries</p>
            </div>
            <Link to="/medications" className="btn-primary">Add Medication</Link>
          </div>
          {dashboard.recentlyAdded.length === 0 ? (
            <EmptyState title="Nothing recent" message="New medication entries will appear here." />
          ) : (
            <MedicationList medications={dashboard.recentlyAdded} compact />
          )}
        </section>
      </div>

      <section className="card">
        <div className="mb-5 flex flex-col justify-between gap-3 sm:flex-row sm:items-center">
          <div>
            <h2 className="text-lg font-bold">Expense Review</h2>
            <p className="text-sm text-slate-500">Simple monthly expense breakdown</p>
          </div>
        </div>
        <div className="space-y-3">
          {dashboard.monthlySeries.map((point) => (
            <div key={point.month} className="grid items-center gap-3 sm:grid-cols-[90px_1fr_120px]">
              <p className="text-sm font-medium text-slate-600">{point.month}</p>
              <div className="h-3 overflow-hidden rounded-full bg-slate-100">
                <div className="h-full rounded-full bg-medify-600" style={{ width: `${(Number(point.total) / maxMonth) * 100}%` }} />
              </div>
              <p className="text-right text-sm font-semibold text-slate-800">{currency(point.total)}</p>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
}

function PageTitle({ title, subtitle, danger = false }) {
  return (
    <div>
      <h1 className="text-2xl font-bold text-slate-900">{title}</h1>
      <p className={`mt-1 text-sm ${danger ? "text-red-600" : "text-slate-500"}`}>{subtitle}</p>
    </div>
  );
}

function StatCard({ label, value, hint }) {
  return (
    <div className="card">
      <p className="text-sm font-medium text-slate-500">{label}</p>
      <p className="mt-2 text-2xl font-bold text-slate-900">{value}</p>
      <p className="mt-1 text-xs text-slate-400">{hint}</p>
    </div>
  );
}

function MedicationList({ medications, compact = false }) {
  return (
    <div className="space-y-3">
      {medications.map((med) => (
        <div key={med.id} className="rounded-xl border border-slate-200 p-3">
          <div className="flex items-start justify-between gap-3">
            <div>
              <p className="font-semibold text-slate-800">{med.medicineName}</p>
              <p className="text-xs text-slate-500">{med.dosage} • Qty {med.quantity} • {med.purpose}</p>
              {!compact && <p className="mt-1 text-xs text-slate-400">Purchased {readableDate(med.purchaseDate)}</p>}
            </div>
            <p className="whitespace-nowrap text-sm font-bold text-slate-900">{currency(med.price)}</p>
          </div>
        </div>
      ))}
    </div>
  );
}
