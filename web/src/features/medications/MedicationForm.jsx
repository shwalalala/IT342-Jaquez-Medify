
import { useEffect, useState } from "react";

const emptyForm = {
  medicineName: "",
  brandName: "",
  dosage: "",
  purpose: "",
  quantity: "",
  price: "",
  purchaseDate: new Date().toISOString().slice(0, 10),
  notes: ""
};

export default function MedicationForm({ editingMedication, onSubmit, onCancel, loading }) {
  const [form, setForm] = useState(emptyForm);

  useEffect(() => {
    if (editingMedication) {
      setForm({
        medicineName: editingMedication.medicineName || "",
        brandName: editingMedication.brandName || "",
        dosage: editingMedication.dosage || "",
        purpose: editingMedication.purpose || "",
        quantity: editingMedication.quantity || "",
        price: editingMedication.price || "",
        purchaseDate: editingMedication.purchaseDate || new Date().toISOString().slice(0, 10),
        notes: editingMedication.notes || ""
      });
    } else {
      setForm(emptyForm);
    }
  }, [editingMedication]);

  function update(field, value) {
    setForm((old) => ({ ...old, [field]: value }));
  }

  function submit(e) {
    e.preventDefault();
    onSubmit({
      ...form,
      quantity: Number(form.quantity),
      price: Number(form.price)
    }).then(() => {
      if (!editingMedication) setForm(emptyForm);
    });
  }

  return (
    <form onSubmit={submit} className="card space-y-4">
      <div>
        <h2 className="text-lg font-bold">{editingMedication ? "Edit Medication Record" : "Add New Medication Record"}</h2>
        <p className="text-sm text-slate-500">Medicine details, inventory quantity, purchase date, and expense.</p>
      </div>

      <div className="grid gap-4 md:grid-cols-2">
        <Field label="Medicine Name" required>
          <input className="input" value={form.medicineName} onChange={(e) => update("medicineName", e.target.value)} placeholder="e.g., Hydroxychloroquine" required />
        </Field>
        <Field label="Brand Name">
          <input className="input" value={form.brandName} onChange={(e) => update("brandName", e.target.value)} placeholder="e.g., Plaquenil" />
        </Field>
        <Field label="Dosage" required>
          <input className="input" value={form.dosage} onChange={(e) => update("dosage", e.target.value)} placeholder="e.g., 200mg" required />
        </Field>
        <Field label="Purpose" required>
          <input className="input" value={form.purpose} onChange={(e) => update("purpose", e.target.value)} placeholder="e.g., SLE" required />
        </Field>
        <Field label="Quantity" required>
          <input className="input" type="number" min="1" value={form.quantity} onChange={(e) => update("quantity", e.target.value)} required />
        </Field>
        <Field label="Price" required>
          <input className="input" type="number" step="0.01" min="0.01" value={form.price} onChange={(e) => update("price", e.target.value)} required />
        </Field>
        <Field label="Date Purchased" required>
          <input className="input" type="date" value={form.purchaseDate} max={new Date().toISOString().slice(0, 10)} onChange={(e) => update("purchaseDate", e.target.value)} required />
        </Field>
      </div>

      <Field label="Notes">
        <textarea className="input min-h-24" value={form.notes} onChange={(e) => update("notes", e.target.value)} placeholder="Optional notes..." />
      </Field>

      <div className="flex flex-col gap-3 sm:flex-row">
        <button className="btn-primary" disabled={loading}>{loading ? "Saving..." : editingMedication ? "Save Changes" : "Add Medication"}</button>
        {editingMedication && <button type="button" className="btn-secondary" onClick={onCancel}>Cancel</button>}
      </div>
    </form>
  );
}

function Field({ label, required, children }) {
  return (
    <div>
      <label className="label">{label} {required && <span className="text-red-500">*</span>}</label>
      {children}
    </div>
  );
}
