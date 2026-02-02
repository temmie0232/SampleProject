import api from "./client";
import { User } from "./types";

export async function signup(email: string, password: string, displayName: string) {
  await api.post("/auth/signup", { email, password, displayName });
}

export async function login(email: string, password: string) {
  const res = await api.post("/auth/login", { email, password });
  return res.data as { accessToken: string; tokenType: string; expiresIn: number };
}

export async function getMe() {
  const res = await api.get<User>("/users/me");
  return res.data;
}

