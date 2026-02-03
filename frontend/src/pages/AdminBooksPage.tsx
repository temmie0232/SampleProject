import { ChangeEvent, useEffect, useState } from "react";
import { addCopies, createBook, getBooks, uploadCover } from "../api/books";
import { getAuthors } from "../api/authors";
import { getCategories } from "../api/categories";
import { Author, Book, Category } from "../api/types";

export default function AdminBooksPage() {
  const [books, setBooks] = useState<Book[]>([]);
  const [authors, setAuthors] = useState<Author[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [authorIds, setAuthorIds] = useState<string[]>([]);
  const [categoryIds, setCategoryIds] = useState<string[]>([]);
  const [coverFiles, setCoverFiles] = useState<Record<string, File | null>>({});

  const load = async () => {
    const [bookRes, authorRes, categoryRes] = await Promise.all([
      getBooks({}),
      getAuthors(),
      getCategories(),
    ]);
    setBooks(bookRes.items);
    setAuthors(authorRes);
    setCategories(categoryRes);
  };

  useEffect(() => {
    load();
  }, []);

  const onCreate = async () => {
    if (!title) return;
    await createBook({ title, description, authorIds, categoryIds });
    setTitle("");
    setDescription("");
    setAuthorIds([]);
    setCategoryIds([]);
    await load();
  };

  const onAddCopy = async (bookId: string) => {
    await addCopies(bookId, 1);
    await load();
  };

  const onCoverChange = (bookId: string, file: File | null) => {
    setCoverFiles((prev) => ({ ...prev, [bookId]: file }));
  };

  const onUploadCover = async (bookId: string) => {
    const file = coverFiles[bookId];
    if (!file) return;
    await uploadCover(bookId, file);
    onCoverChange(bookId, null);
    await load();
  };

  const handleMultiSelect = (
    e: ChangeEvent<HTMLSelectElement>,
    setter: (ids: string[]) => void
  ) => {
    const options = Array.from(e.target.selectedOptions).map((o) => o.value);
    setter(options);
  };

  return (
    <div className="container">
      <h2>図書管理</h2>
      <div className="card" style={{ marginBottom: 16 }}>
        <div className="row">
          <input
            placeholder="タイトル"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
          />
          <textarea
            placeholder="概要"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
        </div>
        <div className="row">
          <select multiple value={authorIds} onChange={(e) => handleMultiSelect(e, setAuthorIds)}>
            {authors.map((a) => (
              <option key={a.id} value={a.id}>
                {a.name}
              </option>
            ))}
          </select>
          <select multiple value={categoryIds} onChange={(e) => handleMultiSelect(e, setCategoryIds)}>
            {categories.map((c) => (
              <option key={c.id} value={c.id}>
                {c.name}
              </option>
            ))}
          </select>
        </div>
        <button className="btn" onClick={onCreate}>
          追加
        </button>
      </div>

      <table className="table">
        <thead>
          <tr>
            <th>タイトル</th>
            <th>著者</th>
            <th>カテゴリ</th>
            <th>在庫</th>
            <th>表紙</th>
          </tr>
        </thead>
        <tbody>
          {books.map((book) => (
            <tr key={book.id}>
              <td>{book.title}</td>
              <td>{book.authors.map((a) => a.name).join(", ")}</td>
              <td>{book.categories.map((c) => c.name).join(", ")}</td>
              <td>
                {book.availableCopies}/{book.totalCopies}
                <button className="btn" style={{ marginLeft: 8 }} onClick={() => onAddCopy(book.id)}>
                  +1冊
                </button>
              </td>
              <td>
                <input
                  type="file"
                  accept="image/*"
                  onChange={(e) => onCoverChange(book.id, e.target.files?.[0] ?? null)}
                />
                <button className="btn" onClick={() => onUploadCover(book.id)} disabled={!coverFiles[book.id]}>
                  表紙アップ
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
