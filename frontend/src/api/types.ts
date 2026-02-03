export type Role = "ADMIN" | "MEMBER";

export type User = {
  id: string;
  email: string;
  displayName: string;
  role: Role;
};

export type Category = {
  id: string;
  name: string;
  active: boolean;
};

export type Author = {
  id: string;
  name: string;
};

export type Book = {
  id: string;
  title: string;
  description?: string;
  coverUrl?: string | null;
  authors: Author[];
  categories: Category[];
  totalCopies: number;
  availableCopies: number;
};

export type Loan = {
  id: string;
  copyId: string;
  bookId: string;
  bookTitle: string;
  borrowerId: string;
  borrowerEmail: string;
  borrowerDisplayName: string;
  status: "ACTIVE" | "RETURNED";
  borrowedAt: string;
  dueDate: string;
  returnedAt?: string | null;
};

export type PageResponse<T> = {
  items: T[];
  page: number;
  size: number;
  totalItems: number;
  totalPages: number;
};
