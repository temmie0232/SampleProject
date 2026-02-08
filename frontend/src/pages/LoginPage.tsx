import { FormEvent, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../features/auth/AuthContext';
import { extractApiError } from '../api/client';
import { required } from '../utils/validation';

export function LoginPage() {
  const auth = useAuth();
  const navigate = useNavigate();

  const [loginId, setLoginId] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  async function onSubmit(event: FormEvent) {
    event.preventDefault();
    setError('');

    const loginIdError = required(loginId, 'ログインID');
    if (loginIdError) {
      setError(loginIdError);
      return;
    }

    const passwordError = required(password, 'パスワード');
    if (passwordError) {
      setError(passwordError);
      return;
    }

    try {
      setSubmitting(true);
      await auth.login(loginId, password);
      navigate('/applications');
    } catch (e) {
      setError(extractApiError(e));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="container" style={{ maxWidth: 520 }}>
      <div className="card stack">
        <h1>ログイン</h1>
        <p className="muted">学習用アカウント: `user01 / user123`、`admin01 / admin123`</p>
        <form onSubmit={onSubmit} className="stack">
          <label>
            ログインID
            <input value={loginId} onChange={(e) => setLoginId(e.target.value)} />
          </label>
          <label>
            パスワード
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
          </label>
          {error ? <div className="error">{error}</div> : null}
          <button className="primary" type="submit" disabled={submitting}>
            {submitting ? 'ログイン中...' : 'ログイン'}
          </button>
        </form>
      </div>
    </div>
  );
}
