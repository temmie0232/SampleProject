import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getBooks } from "../api/books";
import { Book } from "../api/types";

export default function BooksPage() {
  const [books, setBooks] = useState<Book[]>([]);
  const [q, setQ] = useState("");
  const [loading, setLoading] = useState(true);

  const load = async () => {
    setLoading(true);
    const res = await getBooks({ q });
    setBooks(res.items);
    setLoading(false);
  };

  useEffect(() => {
    load();
  }, []);

  return (
    <div className="container">
      <h2>図書検索</h2>
      <div className="row" style={{ marginBottom: 12 }}>
        <input
          placeholder="タイトル・概要で検索"
          value={q}
          onChange={(e) => setQ(e.target.value)}
        />
        <button className="btn" onClick={load}>
          検索
        </button>
      </div>
      {loading ? (
        <div>Loading...</div>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>タイトル</th>
              <th>著者</th>
              <th>カテゴリ</th>
              <th>在庫</th>
            </tr>
          </thead>
          <tbody>
            {books.map((book) => (
              <tr key={book.id}>
                <td>
                  <Link to={`/books/${book.id}`}>{book.title}</Link>
                </td>
                <td>{book.authors.map((a) => a.name).join(", ")}</td>
                <td>{book.categories.map((c) => c.name).join(", ")}</td>
                <td>
                  {book.availableCopies}/{book.totalCopies}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

