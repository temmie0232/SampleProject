import { useEffect, useState } from "react";
import { getMyHistory } from "../api/loans";
import { Loan } from "../api/types";

export default function MyHistoryPage() {
  const [loans, setLoans] = useState<Loan[]>([]);

  useEffect(() => {
    const load = async () => {
      const res = await getMyHistory();
      setLoans(res.items);
    };
    load();
  }, []);

  return (
    <div className="container">
      <h2>貸出履歴</h2>
      <table className="table">
        <thead>
          <tr>
            <th>タイトル</th>
            <th>借りた日</th>
            <th>返却日</th>
          </tr>
        </thead>
        <tbody>
          {loans.map((loan) => (
            <tr key={loan.id}>
              <td>{loan.bookTitle}</td>
              <td>{new Date(loan.borrowedAt).toLocaleDateString()}</td>
              <td>{loan.returnedAt ? new Date(loan.returnedAt).toLocaleDateString() : "-"}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

