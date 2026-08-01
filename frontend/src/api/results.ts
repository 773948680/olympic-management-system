import { apiClient } from './client'
import type { ResultRequest, ResultResponse } from '@/types'

const BASE = '/results'

export const resultsApi = {
  get: (id: number) =>
    apiClient.get<ResultResponse>(`${BASE}/${id}`).then((res) => res.data),

  create: (data: ResultRequest) =>
    apiClient.post<ResultResponse>(BASE, data).then((res) => res.data),

  update: (id: number, data: ResultRequest) =>
    apiClient.put<ResultResponse>(`${BASE}/${id}`, data).then((res) => res.data),

  remove: (id: number) => apiClient.delete<void>(`${BASE}/${id}`).then((res) => res.data),
}
