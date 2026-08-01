import { apiClient } from './client'
import type {
  CountResponse,
  CountryMedalistsEntry,
  CountryRankingEntry,
  MedalTotalsResponse,
} from '@/types'

const BASE = '/dashboard'

export const dashboardApi = {
  athletesCount: () =>
    apiClient.get<CountResponse>(`${BASE}/athletes/count`).then((res) => res.data),

  countriesCount: () =>
    apiClient.get<CountResponse>(`${BASE}/countries/count`).then((res) => res.data),

  medals: () => apiClient.get<MedalTotalsResponse>(`${BASE}/medals`).then((res) => res.data),

  countriesRanking: () =>
    apiClient
      .get<CountryRankingEntry[]>(`${BASE}/countries/ranking`)
      .then((res) => res.data),

  countriesMedalists: () =>
    apiClient
      .get<CountryMedalistsEntry[]>(`${BASE}/countries/medalists`)
      .then((res) => res.data),
}
