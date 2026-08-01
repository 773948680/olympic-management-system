import { apiClient } from './client'
import type { LoginRequest, LoginResponse, MeResponse } from '@/types'

const BASE = '/auth'

export const authApi = {
  login: (data: LoginRequest) =>
    apiClient.post<LoginResponse>(`${BASE}/login`, data).then((res) => res.data),

  me: () => apiClient.get<MeResponse>(`${BASE}/me`).then((res) => res.data),
}
