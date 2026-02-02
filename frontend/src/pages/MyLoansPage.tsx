import { useEffect, useState } from "react";
import { getMyLoans, returnLoan } from "../api/loans";
import { Loan } from "../api/types";

export default function MyLoansPage() {
  const [loans, setLoans] = useState<Loan[]>([]);

  const load = async () => {
    const res = await getMyLoans();
    setLoans(res.items);
  };

  useEffect(() => {
    load();
  }, []);

  const onReturn = async (loanId: string) => {
    await returnLoan(loanId);
    await load();
  };

  return (
    <div className="container">
      <h2>貸出中</h2>
      <table className="table">
        <thead>
          <tr>
            <th>タイトル</th>
            <th>期限</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {loans.map((loan) => (
            <tr key={loan.id}>
              <td>{loan.bookTitle}</td>
              <td>{loan.dueDate}</td>
              <td>
                <button className="btn" onClick={() => onReturn(loan.id)}>
                  返却
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

