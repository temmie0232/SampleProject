import { Link, Navigate, Route, Routes } from "react-router-dom";
import RequireAuth from "./components/RequireAuth";
import { useAuth } from "./state/AuthContext";
import LoginPage from "./pages/LoginPage";
import SignupPage from "./pages/SignupPage";
import BooksPage from "./pages/BooksPage";
import BookDetailPage from "./pages/BookDetailPage";
import MyLoansPage from "./pages/MyLoansPage";
import MyHistoryPage from "./pages/MyHistoryPage";
import AdminCategoriesPage from "./pages/AdminCategoriesPage";
import AdminAuthorsPage from "./pages/AdminAuthorsPage";
import AdminBooksPage from "./pages/AdminBooksPage";

export default function App() {
  const { user, logout } = useAuth();

  return (
    <>
      <div className="nav">
        <Link to="/books">図書</Link>
        <Link to="/loans">貸出中</Link>
        <Link to="/history">履歴</Link>
        {user?.role === "ADMIN" && (
          <>
            <Link to="/admin/books">図書管理</Link>
            <Link to="/admin/categories">カテゴリ管理</Link>
            <Link to="/admin/authors">著者管理</Link>
          </>
        )}
        {user ? (
          <button className="btn secondary" style={{ float: "right" }} onClick={logout}>
            ログアウト
          </button>
        ) : (
          <Link to="/login" style={{ float: "right" }}>
            ログイン
          </Link>
        )}
      </div>
      <Routes>
        <Route path="/" element={<Navigate to="/books" replace />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route
          path="/books"
          element={
            <RequireAuth>
              <BooksPage />
            </RequireAuth>
          }
        />
        <Route
          path="/books/:bookId"
          element={
            <RequireAuth>
              <BookDetailPage />
            </RequireAuth>
          }
        />
        <Route
          path="/loans"
          element={
            <RequireAuth>
              <MyLoansPage />
            </RequireAuth>
          }
        />
        <Route
          path="/history"
          element={
            <RequireAuth>
              <MyHistoryPage />
            </RequireAuth>
          }
        />
        <Route
          path="/admin/categories"
          element={
            <RequireAuth>
              <AdminCategoriesPage />
            </RequireAuth>
          }
        />
        <Route
          path="/admin/authors"
          element={
            <RequireAuth>
              <AdminAuthorsPage />
            </RequireAuth>
          }
        />
        <Route
          path="/admin/books"
          element={
            <RequireAuth>
              <AdminBooksPage />
            </RequireAuth>
          }
        />
      </Routes>
    </>
  );
}

