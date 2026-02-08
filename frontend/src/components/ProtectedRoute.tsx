import { Navigate } from 'react-router-dom';
import { useAuth } from '../features/auth/AuthContext';
import type { Role } from '../api/types';

interface Props {
  children: JSX.Element;
  roles?: Role[];
}

export function ProtectedRoute({ children, roles }: Props) {
  const auth = useAuth();

  if (auth.loading) {
    return <div className="container">認証情報を確認中...</div>;
  }

  if (!auth.isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (roles && auth.user && !roles.includes(auth.user.role)) {
    return <Navigate to="/applications" replace />;
  }

  return children;
}
