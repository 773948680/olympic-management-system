import { useMutation, useQueryClient } from '@tanstack/react-query'
import { resultsApi } from '@/api'
import { eventKeys } from './events'
import type { ResultRequest } from '@/types'

export function useCreateResult(eventId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (data: ResultRequest) => resultsApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: eventKeys.results(eventId) })
      queryClient.invalidateQueries({ queryKey: eventKeys.podium(eventId) })
      queryClient.invalidateQueries({ queryKey: ['dashboard'] })
      queryClient.invalidateQueries({ queryKey: ['medals'] })
    },
  })
}

export function useUpdateResult(eventId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: ResultRequest }) => resultsApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: eventKeys.results(eventId) })
      queryClient.invalidateQueries({ queryKey: eventKeys.podium(eventId) })
      queryClient.invalidateQueries({ queryKey: ['dashboard'] })
      queryClient.invalidateQueries({ queryKey: ['medals'] })
    },
  })
}

export function useDeleteResult(eventId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => resultsApi.remove(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: eventKeys.results(eventId) })
      queryClient.invalidateQueries({ queryKey: eventKeys.podium(eventId) })
      queryClient.invalidateQueries({ queryKey: ['dashboard'] })
      queryClient.invalidateQueries({ queryKey: ['medals'] })
    },
  })
}
