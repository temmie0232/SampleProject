import { useEffect, useState } from "react";
import { createAuthor, getAuthors } from "../api/authors";
import { Author } from "../api/types";

export default function AdminAuthorsPage() {
  const [authors, setAuthors] = useState<Author[]>([]);
  const [name, setName] = useState("");

  const load = async () => {
    const res = await getAuthors();
    setAuthors(res);
  };

  useEffect(() => {
    load();
  }, []);

  const onCreate = async () => {
    if (!name) return;
    await createAuthor(name);
    setName("");
    await load();
  };

  return (
    <div className="container">
      <h2>著者管理</h2>
      <div className="row" style={{ marginBottom: 12 }}>
        <input
          placeholder="著者名"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
        <button className="btn" onClick={onCreate}>
          追加
        </button>
      </div>
      <ul>
        {authors.map((a) => (
          <li key={a.id}>{a.name}</li>
        ))}
      </ul>
    </div>
  );
}

