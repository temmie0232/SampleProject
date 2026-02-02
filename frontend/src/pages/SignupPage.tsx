import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../state/AuthContext";

export default function SignupPage() {
  const { signup } = useAuth();
  const [email, setEmail] = useState("");
  const [displayName, setDisplayName] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    try {
      await signup(email, password, displayName);
      navigate("/login");
    } catch {
      setError("サインアップに失敗しました");
    }
  };

  return (
    <div className="container">
      <div className="card" style={{ maxWidth: 420, margin: "40px auto" }}>
        <h2>サインアップ</h2>
        <form onSubmit={onSubmit}>
          <div className="row">
            <input
              placeholder="Email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
            <input
              placeholder="Display Name"
              value={displayName}
              onChange={(e) => setDisplayName(e.target.value)}
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
            登録
          </button>
        </form>
        <p className="muted" style={{ marginTop: 12 }}>
          すでにアカウントがある場合は <Link to="/login">ログイン</Link>
        </p>
      </div>
    </div>
  );
}

