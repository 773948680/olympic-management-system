import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { disciplinesApi } from '@/api'
import type { DisciplineRequest, PageableParams } from '@/types'

export const disciplineKeys = {
  all: ['disciplines'] as const,
  list: (params?: PageableParams) => [...disciplineKeys.all, 'list', params ?? {}] as const,
}

export function useDisciplines(params?: PageableParams) {
  return useQuery({
    queryKey: disciplineKeys.list(params),
    queryFn: () => disciplinesApi.list(params),
    placeholderData: (previousData) => previousData,
  })
}

export function useCreateDiscipline() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (data: DisciplineRequest) => disciplinesApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: disciplineKeys.all })
    },
  })
}

export function useUpdateDiscipline() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: DisciplineRequest }) =>
      disciplinesApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: disciplineKeys.all })
    },
  })
}

export function useDeleteDiscipline() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => disciplinesApi.remove(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: disciplineKeys.all })
    },
  })
}
