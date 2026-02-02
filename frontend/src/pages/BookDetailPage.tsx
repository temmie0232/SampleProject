import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { borrowBook, getBook } from "../api/books";
import { Book } from "../api/types";

export default function BookDetailPage() {
  const { bookId } = useParams();
  const [book, setBook] = useState<Book | null>(null);
  const [message, setMessage] = useState<string | null>(null);

  const load = async () => {
    if (!bookId) return;
    const data = await getBook(bookId);
    setBook(data);
  };

  useEffect(() => {
    load();
  }, [bookId]);

  const onBorrow = async () => {
    if (!bookId) return;
    try {
      await borrowBook(bookId);
      setMessage("貸出しました");
      await load();
    } catch {
      setMessage("貸出できませんでした");
    }
  };

  if (!book) return <div className="container">Loading...</div>;

  return (
    <div className="container">
      <div className="card">
        <h2>{book.title}</h2>
        <p className="muted">{book.description}</p>
        <p>著者: {book.authors.map((a) => a.name).join(", ")}</p>
        <p>カテゴリ: {book.categories.map((c) => c.name).join(", ")}</p>
        <p>
          在庫: {book.availableCopies}/{book.totalCopies}
        </p>
        {book.coverUrl && (
          <img src={book.coverUrl} alt={book.title} style={{ maxWidth: 240 }} />
        )}
        <div style={{ marginTop: 12 }}>
          <button className="btn" onClick={onBorrow} disabled={book.availableCopies < 1}>
            借りる
          </button>
          {message && <span style={{ marginLeft: 12 }}>{message}</span>}
        </div>
      </div>
    </div>
  );
}

