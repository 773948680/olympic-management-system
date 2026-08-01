import { useQuery } from '@tanstack/react-query'
import { medalsApi } from '@/api'

export const medalKeys = {
  all: ['medals'] as const,
  table: () => [...medalKeys.all, 'table'] as const,
}

export function useMedalTable() {
  return useQuery({
    queryKey: medalKeys.table(),
    queryFn: medalsApi.table,
  })
}
