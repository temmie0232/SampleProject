import api from "./client";
import { Author } from "./types";

export async function getAuthors() {
  const res = await api.get<Author[]>("/authors");
  return res.data;
}

export async function createAuthor(name: string) {
  const res = await api.post<Author>("/authors", { name });
  return res.data;
}

