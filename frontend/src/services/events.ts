import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { eventsApi } from '@/api'
import type { EventRequest, EventSearchParams, PageableParams } from '@/types'

export const eventKeys = {
  all: ['events'] as const,
  list: (params?: EventSearchParams & PageableParams) => [...eventKeys.all, 'list', params ?? {}] as const,
  detail: (id: number) => [...eventKeys.all, 'detail', id] as const,
  podium: (id: number) => [...eventKeys.all, 'podium', id] as const,
  results: (id: number) => [...eventKeys.all, 'results', id] as const,
}

export function useEvents(params?: EventSearchParams & PageableParams) {
  return useQuery({
    queryKey: eventKeys.list(params),
    queryFn: () => eventsApi.search(params),
    placeholderData: (previousData) => previousData,
  })
}

export function useEvent(id: number | undefined) {
  return useQuery({
    queryKey: eventKeys.detail(id ?? 0),
    queryFn: () => eventsApi.get(id as number),
    enabled: id !== undefined,
  })
}

export function useEventPodium(id: number | undefined) {
  return useQuery({
    queryKey: eventKeys.podium(id ?? 0),
    queryFn: () => eventsApi.podium(id as number),
    enabled: id !== undefined,
  })
}

export function useEventResults(id: number | undefined) {
  return useQuery({
    queryKey: eventKeys.results(id ?? 0),
    queryFn: () => eventsApi.results(id as number),
    enabled: id !== undefined,
  })
}

export function useCreateEvent() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (data: EventRequest) => eventsApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: eventKeys.all })
    },
  })
}

export function useUpdateEvent() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: EventRequest }) => eventsApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: eventKeys.all })
    },
  })
}

export function useDeleteEvent() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => eventsApi.remove(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: eventKeys.all })
    },
  })
}
