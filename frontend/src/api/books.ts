import api from "./client";
import { Book, PageResponse } from "./types";

export async function getBooks(params: {
  q?: string;
  authorId?: string;
  categoryId?: string;
  page?: number;
  size?: number;
}) {
  const res = await api.get<PageResponse<Book>>("/books", { params });
  return res.data;
}

export async function getBook(id: string) {
  const res = await api.get<Book>(`/books/${id}`);
  return res.data;
}

export async function borrowBook(bookId: string) {
  const res = await api.post(`/books/${bookId}/borrow`);
  return res.data;
}

export async function createBook(input: {
  title: string;
  description?: string;
  authorIds: string[];
  categoryIds: string[];
}) {
  const res = await api.post<Book>("/books", input);
  return res.data;
}

export async function addCopies(bookId: string, count = 1) {
  const res = await api.post(`/books/${bookId}/copies`, { count });
  return res.data;
}
