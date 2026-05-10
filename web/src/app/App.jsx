import React from "react";
import { Navigate, Route, Routes } from "react-router-dom";
import LoginPage from "../features/auth/LoginPage.jsx";
import RegisterPage from "../features/auth/RegisterPage.jsx";
import VerifyEmailPage from "../features/auth/VerifyEmailPage.jsx";
import ProtectedRoute from "../features/auth/ProtectedRoute.jsx";
import DashboardPage from "../features/dashboard/DashboardPage.jsx";
import MedicationsPage from "../features/medications/MedicationsPage.jsx";
import ProfilePage from "../features/profile/ProfilePage.jsx";
import Layout from "../components/Layout.jsx";

export default function App() {
  return (  
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/verify-email" element={<VerifyEmailPage />} />

      <Route
        path="/"
        element={
          <ProtectedRoute>
            <Layout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<DashboardPage />} />
        <Route path="medications" element={<MedicationsPage />} />
        <Route path="profile" element={<ProfilePage />} />
      </Route>

      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}
