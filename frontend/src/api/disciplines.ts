import { apiClient } from './client'
import type {
  AthleteResponse,
  DisciplinePatchRequest,
  DisciplineRequest,
  DisciplineResponse,
  PageableParams,
  PageResponse,
} from '@/types'

const BASE = '/disciplines'

export const disciplinesApi = {
  list: (params?: PageableParams) =>
    apiClient
      .get<PageResponse<DisciplineResponse>>(BASE, { params })
      .then((res) => res.data),

  get: (id: number) =>
    apiClient.get<DisciplineResponse>(`${BASE}/${id}`).then((res) => res.data),

  create: (data: DisciplineRequest) =>
    apiClient.post<DisciplineResponse>(BASE, data).then((res) => res.data),

  update: (id: number, data: DisciplineRequest) =>
    apiClient.put<DisciplineResponse>(`${BASE}/${id}`, data).then((res) => res.data),

  patch: (id: number, data: DisciplinePatchRequest) =>
    apiClient.patch<DisciplineResponse>(`${BASE}/${id}`, data).then((res) => res.data),

  remove: (id: number) => apiClient.delete<void>(`${BASE}/${id}`).then((res) => res.data),

  athletes: (id: number, params?: PageableParams) =>
    apiClient
      .get<PageResponse<AthleteResponse>>(`${BASE}/${id}/athletes`, { params })
      .then((res) => res.data),
}
