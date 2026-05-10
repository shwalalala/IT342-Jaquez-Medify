import React from "react";
import { useEffect, useMemo, useState } from "react";
import api, { apiError, unwrap } from "../../app/api.js";
import { currency } from "../../utils/format.js";
import MedicationForm from "./MedicationForm.jsx";
import MedicationTable from "./MedicationTable.jsx";

export default function MedicationsPage() {
  const [medications, setMedications] = useState([]);
  const [search, setSearch] = useState("");
  const [period, setPeriod] = useState("all");
  const [sort, setSort] = useState("date_desc");
  const [editing, setEditing] = useState(null);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const total = useMemo(() => medications.reduce((sum, med) => sum + Number(med.price || 0), 0), [medications]);

  useEffect(() => {
    loadMedications();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [period, sort]);

  async function loadMedications(customSearch = search) {
    setError("");
    try {
      const response = await api.get("/medications", { params: { search: customSearch, period, sort } });
      setMedications(unwrap(response));
    } catch (err) {
      setError(apiError(err));
    }
  }

  async function submitForm(payload) {
    setLoading(true);
    setError("");
    setMessage("");
    try {
      if (editing) {
        await api.put(`/medications/${editing.id}`, payload);
        setMessage("Medication updated successfully.");
      } else {
        await api.post("/medications", payload);
        setMessage("Medication added successfully.");
      }
      setEditing(null);
      await loadMedications();
    } catch (err) {
      setError(apiError(err));
    } finally {
      setLoading(false);
    }
  }

  async function deleteMedication(medication) {
    const ok = window.confirm(`Delete ${medication.medicineName}? This action cannot be undone.`);
    if (!ok) return;

    setError("");
    setMessage("");
    try {
      await api.delete(`/medications/${medication.id}`);
      setMessage("Medication deleted successfully.");
      await loadMedications();
    } catch (err) {
      setError(apiError(err));
    }
  }

  function submitSearch(e) {
    e.preventDefault();
    loadMedications(search);
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-3 sm:flex-row sm:items-end">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Medication Management</h1>
          <p className="mt-1 text-sm text-slate-500">Add, edit, delete, filter, and review medication purchases.</p>
        </div>
        <div className="card !p-4 text-right">
          <p className="text-xs font-medium uppercase tracking-wide text-slate-500">Filtered total</p>
          <p className="text-2xl font-bold text-slate-900">{currency(total)}</p>
        </div>
      </div>

      {message && <div className="rounded-xl bg-emerald-50 p-3 text-sm text-emerald-700">{message}</div>}
      {error && <div className="rounded-xl bg-red-50 p-3 text-sm text-red-700">{error}</div>}

      <section className="card">
        <form onSubmit={submitSearch} className="grid gap-3 lg:grid-cols-[1fr_180px_180px_auto]">
          <input className="input" value={search} onChange={(e) => setSearch(e.target.value)} placeholder="Search medication, brand, or purpose..." />
          <select className="input" value={period} onChange={(e) => setPeriod(e.target.value)}>
            <option value="all">All time</option>
            <option value="week">This week</option>
            <option value="month">This month</option>
            <option value="lastMonth">Last month</option>
          </select>
          <select className="input" value={sort} onChange={(e) => setSort(e.target.value)}>
            <option value="date_desc">Newest first</option>
            <option value="date_asc">Oldest first</option>
            <option value="name">Name A-Z</option>
            <option value="price">Highest price</option>
          </select>
          <button className="btn-primary" type="submit">Apply</button>
        </form>
      </section>

      <MedicationTable medications={medications} onEdit={setEditing} onDelete={deleteMedication} />

      <MedicationForm
        editingMedication={editing}
        onSubmit={submitForm}
        onCancel={() => setEditing(null)}
        loading={loading}
      />
    </div>
  );
}
