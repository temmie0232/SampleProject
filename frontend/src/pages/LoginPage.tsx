import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../state/AuthContext";

export default function LoginPage() {
  const { login } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    try {
      await login(email, password);
      navigate("/books");
    } catch {
      setError("ログインに失敗しました");
    }
  };

  return (
    <div className="container">
      <div className="card" style={{ maxWidth: 420, margin: "40px auto" }}>
        <h2>ログイン</h2>
        <form onSubmit={onSubmit}>
          <div className="row">
            <input
              placeholder="Email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
            <input
              placeholder="Password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>
          {error && <p className="muted">{error}</p>}
          <button className="btn" type="submit">
            ログイン
          </button>
        </form>
        <p className="muted" style={{ marginTop: 12 }}>
          アカウントがない場合は <Link to="/signup">サインアップ</Link>
        </p>
      </div>
    </div>
  );
}

