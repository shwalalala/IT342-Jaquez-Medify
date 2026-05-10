import React from "react";
import EmptyState from "../../components/EmptyState.jsx";
import { currency, readableDate } from "../../utils/format.js";

export default function MedicationTable({ medications, onEdit, onDelete }) {
  if (!medications.length) {
    return <EmptyState title="No medications yet" message="Add your first medication record using the form below." />;
  }

  return (
    <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white">
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-slate-200 text-sm">
          <thead className="bg-slate-50 text-left text-xs uppercase tracking-wide text-slate-500">
            <tr>
              <th className="px-4 py-3">Medication Name</th>
              <th className="px-4 py-3">Dosage</th>
              <th className="px-4 py-3">Purpose</th>
              <th className="px-4 py-3">Quantity</th>
              <th className="px-4 py-3">Price</th>
              <th className="px-4 py-3">Date</th>
              <th className="px-4 py-3 text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {medications.map((med) => (
              <tr key={med.id} className="hover:bg-slate-50">
                <td className="px-4 py-3">
                  <p className="font-semibold text-slate-800">{med.medicineName}</p>
                  {med.brandName && <p className="text-xs text-slate-500">{med.brandName}</p>}
                </td>
                <td className="px-4 py-3 text-slate-600">{med.dosage}</td>
                <td className="px-4 py-3 text-slate-600">{med.purpose}</td>
                <td className="px-4 py-3 text-slate-600">{med.quantity}</td>
                <td className="px-4 py-3 font-semibold text-slate-800">{currency(med.price)}</td>
                <td className="px-4 py-3 text-slate-600">{readableDate(med.purchaseDate)}</td>
                <td className="px-4 py-3">
                  <div className="flex justify-end gap-2">
                    <button className="btn-secondary !min-h-9 !px-3" onClick={() => onEdit(med)}>Edit</button>
                    <button className="btn-danger !min-h-9 !px-3" onClick={() => onDelete(med)}>Delete</button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
