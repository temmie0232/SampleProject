import { Link, Outlet } from 'react-router-dom';
import { useAuth } from '../features/auth/AuthContext';

export function Layout() {
  const auth = useAuth();

  return (
    <>
      <header className="header">
        <div className="header-inner">
          <div className="brand">住所変更申請ワークフロー</div>
          <nav className="nav">
            <Link to="/applications">申請一覧</Link>
            <Link to="/applications/new">新規申請</Link>
            {auth.user?.role === 'ADMIN' ? <Link to="/audit-logs">監査ログ</Link> : null}
            <button type="button" onClick={auth.logout}>ログアウト</button>
          </nav>
        </div>
      </header>
      <main className="container">
        <Outlet />
      </main>
    </>
  );
}
