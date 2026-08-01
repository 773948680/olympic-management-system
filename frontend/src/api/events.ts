import { apiClient } from './client'
import type {
  EventPatchRequest,
  EventRequest,
  EventResponse,
  EventSearchParams,
  PageableParams,
  PageResponse,
  ResultResponse,
} from '@/types'

const BASE = '/events'

export const eventsApi = {
  search: (params?: EventSearchParams & PageableParams) =>
    apiClient
      .get<PageResponse<EventResponse>>(BASE, { params })
      .then((res) => res.data),

  get: (id: number) =>
    apiClient.get<EventResponse>(`${BASE}/${id}`).then((res) => res.data),

  create: (data: EventRequest) =>
    apiClient.post<EventResponse>(BASE, data).then((res) => res.data),

  update: (id: number, data: EventRequest) =>
    apiClient.put<EventResponse>(`${BASE}/${id}`, data).then((res) => res.data),

  patch: (id: number, data: EventPatchRequest) =>
    apiClient.patch<EventResponse>(`${BASE}/${id}`, data).then((res) => res.data),

  remove: (id: number) => apiClient.delete<void>(`${BASE}/${id}`).then((res) => res.data),

  results: (eventId: number) =>
    apiClient.get<ResultResponse[]>(`${BASE}/${eventId}/results`).then((res) => res.data),

  podium: (eventId: number) =>
    apiClient.get<ResultResponse[]>(`${BASE}/${eventId}/podium`).then((res) => res.data),
}
