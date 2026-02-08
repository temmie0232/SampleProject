import { Navigate, Route, Routes } from 'react-router-dom';
import { ProtectedRoute } from '../components/ProtectedRoute';
import { Layout } from '../components/Layout';
import { LoginPage } from '../pages/LoginPage';
import { ApplicationListPage } from '../pages/ApplicationListPage';
import { ApplicationCreatePage } from '../pages/ApplicationCreatePage';
import { ApplicationDetailPage } from '../pages/ApplicationDetailPage';
import { AuditLogPage } from '../pages/AuditLogPage';

export function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />

      <Route
        path="/"
        element={
          <ProtectedRoute>
            <Layout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="/applications" replace />} />
        <Route path="applications" element={<ApplicationListPage />} />
        <Route
          path="applications/new"
          element={
            <ProtectedRoute roles={['USER']}>
              <ApplicationCreatePage />
            </ProtectedRoute>
          }
        />
        <Route path="applications/:id" element={<ApplicationDetailPage />} />
        <Route
          path="audit-logs"
          element={
            <ProtectedRoute roles={['ADMIN']}>
              <AuditLogPage />
            </ProtectedRoute>
          }
        />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
