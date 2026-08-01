import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { athletesApi } from '@/api'
import type { AthleteRequest, AthleteSearchParams, PageableParams } from '@/types'

export const athleteKeys = {
  all: ['athletes'] as const,
  list: (params?: AthleteSearchParams & PageableParams) => [...athleteKeys.all, 'list', params ?? {}] as const,
}

export function useAthletes(params?: AthleteSearchParams & PageableParams) {
  return useQuery({
    queryKey: athleteKeys.list(params),
    queryFn: () => athletesApi.search(params),
    placeholderData: (previousData) => previousData,
  })
}

export function useCreateAthlete() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (data: AthleteRequest) => athletesApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: athleteKeys.all })
      queryClient.invalidateQueries({ queryKey: ['dashboard'] })
      queryClient.invalidateQueries({ queryKey: ['statistics'] })
    },
  })
}

export function useUpdateAthlete() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: AthleteRequest }) => athletesApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: athleteKeys.all })
      queryClient.invalidateQueries({ queryKey: ['dashboard'] })
      queryClient.invalidateQueries({ queryKey: ['statistics'] })
    },
  })
}

export function useDeleteAthlete() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => athletesApi.remove(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: athleteKeys.all })
      queryClient.invalidateQueries({ queryKey: ['dashboard'] })
      queryClient.invalidateQueries({ queryKey: ['statistics'] })
    },
  })
}
