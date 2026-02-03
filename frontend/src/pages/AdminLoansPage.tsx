import { useEffect, useState } from "react";
import { getAdminLoans } from "../api/loans";
import { Loan, PageResponse } from "../api/types";

export default function AdminLoansPage() {
  const [items, setItems] = useState<Loan[]>([]);
  const [status, setStatus] = useState("");
  const [borrowerEmail, setBorrowerEmail] = useState("");
  const [q, setQ] = useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const load = async (pageNo = 0) => {
    const res: PageResponse<Loan> = await getAdminLoans({
      status: status || undefined,
      borrowerEmail: borrowerEmail || undefined,
      q: q || undefined,
      page: pageNo,
      size: 20,
      sort: "borrowedAt,desc",
    });
    setItems(res.items);
    setPage(res.page);
    setTotalPages(res.totalPages);
  };

  useEffect(() => {
    load(0);
  }, []);

  return (
    <div className="container">
      <h2>貸出一覧（管理者）</h2>
      <div className="row" style={{ marginBottom: 12 }}>
        <input
          placeholder="タイトル検索"
          value={q}
          onChange={(e) => setQ(e.target.value)}
        />
        <input
          placeholder="借りた人のEmail"
          value={borrowerEmail}
          onChange={(e) => setBorrowerEmail(e.target.value)}
        />
        <select value={status} onChange={(e) => setStatus(e.target.value)}>
          <option value="">全ステータス</option>
          <option value="ACTIVE">ACTIVE</option>
          <option value="RETURNED">RETURNED</option>
        </select>
        <button className="btn" onClick={() => load(0)}>
          検索
        </button>
      </div>
      <table className="table">
        <thead>
          <tr>
            <th>タイトル</th>
            <th>借りた人</th>
            <th>ステータス</th>
            <th>借りた日</th>
            <th>期限</th>
            <th>返却日</th>
          </tr>
        </thead>
        <tbody>
          {items.map((loan) => (
            <tr key={loan.id}>
              <td>{loan.bookTitle}</td>
              <td>{loan.borrowerDisplayName} ({loan.borrowerEmail})</td>
              <td>{loan.status}</td>
              <td>{new Date(loan.borrowedAt).toLocaleDateString()}</td>
              <td>{loan.dueDate}</td>
              <td>{loan.returnedAt ? new Date(loan.returnedAt).toLocaleDateString() : "-"}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <div className="row" style={{ marginTop: 12 }}>
        <button className="btn secondary" disabled={page <= 0} onClick={() => load(page - 1)}>
          前へ
        </button>
        <div className="muted">Page {page + 1} / {totalPages}</div>
        <button className="btn" disabled={page + 1 >= totalPages} onClick={() => load(page + 1)}>
          次へ
        </button>
      </div>
    </div>
  );
}

