import { apiClient } from './client'
import type {
  AthletePatchRequest,
  AthleteRequest,
  AthleteResponse,
  AthleteSearchParams,
  PageableParams,
  PageResponse,
} from '@/types'

const BASE = '/athletes'

export const athletesApi = {
  search: (params?: AthleteSearchParams & PageableParams) =>
    apiClient
      .get<PageResponse<AthleteResponse>>(BASE, { params })
      .then((res) => res.data),

  get: (id: number) =>
    apiClient.get<AthleteResponse>(`${BASE}/${id}`).then((res) => res.data),

  create: (data: AthleteRequest) =>
    apiClient.post<AthleteResponse>(BASE, data).then((res) => res.data),

  update: (id: number, data: AthleteRequest) =>
    apiClient.put<AthleteResponse>(`${BASE}/${id}`, data).then((res) => res.data),

  patch: (id: number, data: AthletePatchRequest) =>
    apiClient.patch<AthleteResponse>(`${BASE}/${id}`, data).then((res) => res.data),

  remove: (id: number) => apiClient.delete<void>(`${BASE}/${id}`).then((res) => res.data),
}
