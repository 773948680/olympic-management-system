import { apiClient } from './client'
import type { MedalTableEntry } from '@/types'

export const medalsApi = {
  table: () => apiClient.get<MedalTableEntry[]>('/medals/medal-table').then((res) => res.data),
}
