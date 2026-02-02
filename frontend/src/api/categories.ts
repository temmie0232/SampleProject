import api from "./client";
import { Category } from "./types";

export async function getCategories() {
  const res = await api.get<Category[]>("/categories");
  return res.data;
}

export async function createCategory(name: string) {
  const res = await api.post<Category>("/categories", { name });
  return res.data;
}

