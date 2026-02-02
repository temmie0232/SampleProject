import { useEffect, useState } from "react";
import { createCategory, getCategories } from "../api/categories";
import { Category } from "../api/types";

export default function AdminCategoriesPage() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [name, setName] = useState("");

  const load = async () => {
    const res = await getCategories();
    setCategories(res);
  };

  useEffect(() => {
    load();
  }, []);

  const onCreate = async () => {
    if (!name) return;
    await createCategory(name);
    setName("");
    await load();
  };

  return (
    <div className="container">
      <h2>カテゴリ管理</h2>
      <div className="row" style={{ marginBottom: 12 }}>
        <input
          placeholder="カテゴリ名"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
        <button className="btn" onClick={onCreate}>
          追加
        </button>
      </div>
      <ul>
        {categories.map((c) => (
          <li key={c.id}>
            {c.name} {c.active ? "" : "(inactive)"}
          </li>
        ))}
      </ul>
    </div>
  );
}

