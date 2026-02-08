import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { loginApi, meApi } from '../../api/authApi';
import type { CurrentUserResponse, LoginResponse, Role } from '../../api/types';

interface AuthContextType {
  user: CurrentUserResponse | null;
  loading: boolean;
  login: (loginId: string, password: string) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
  hasRole: (role: Role) => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<CurrentUserResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // 画面リロード後もログイン状態を復元し、ガード判定を一元化する。
    async function bootstrap() {
      const token = localStorage.getItem('accessToken');
      if (!token) {
        setLoading(false);
        return;
      }
      try {
        const currentUser = await meApi();
        setUser(currentUser);
      } catch {
        localStorage.removeItem('accessToken');
        setUser(null);
      } finally {
        setLoading(false);
      }
    }

    bootstrap();
  }, []);

  async function login(loginId: string, password: string) {
    const loginResult: LoginResponse = await loginApi({ loginId, password });
    localStorage.setItem('accessToken', loginResult.accessToken);

    // ログイン直後はトークンからも表示できるが、サーバー基準で /me を再取得する。
    const currentUser = await meApi();
    setUser(currentUser);
  }

  function logout() {
    localStorage.removeItem('accessToken');
    setUser(null);
  }

  const value = useMemo<AuthContextType>(
    () => ({
      user,
      loading,
      login,
      logout,
      isAuthenticated: !!user,
      hasRole: (role) => user?.role === role
    }),
    [user, loading]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used inside AuthProvider');
  }
  return context;
}
