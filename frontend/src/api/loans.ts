import api from "./client";
import { Loan, PageResponse } from "./types";

export async function getMyLoans() {
  const res = await api.get<PageResponse<Loan>>("/loans/me");
  return res.data;
}

export async function getMyHistory() {
  const res = await api.get<PageResponse<Loan>>("/loans/me/history");
  return res.data;
}

export async function returnLoan(loanId: string) {
  const res = await api.post<Loan>(`/loans/${loanId}/return`);
  return res.data;
}

