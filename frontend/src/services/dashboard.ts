import { useQuery } from '@tanstack/react-query'
import { dashboardApi } from '@/api'

export const dashboardKeys = {
  all: ['dashboard'] as const,
  athletesCount: () => [...dashboardKeys.all, 'athletes-count'] as const,
  countriesCount: () => [...dashboardKeys.all, 'countries-count'] as const,
  medals: () => [...dashboardKeys.all, 'medals'] as const,
  countriesRanking: () => [...dashboardKeys.all, 'countries-ranking'] as const,
  countriesMedalists: () => [...dashboardKeys.all, 'countries-medalists'] as const,
}

export function useAthletesCount() {
  return useQuery({
    queryKey: dashboardKeys.athletesCount(),
    queryFn: dashboardApi.athletesCount,
  })
}

export function useCountriesCount() {
  return useQuery({
    queryKey: dashboardKeys.countriesCount(),
    queryFn: dashboardApi.countriesCount,
  })
}

export function useMedalTotals() {
  return useQuery({
    queryKey: dashboardKeys.medals(),
    queryFn: dashboardApi.medals,
  })
}

export function useCountriesRanking() {
  return useQuery({
    queryKey: dashboardKeys.countriesRanking(),
    queryFn: dashboardApi.countriesRanking,
  })
}

export function useCountriesMedalists() {
  return useQuery({
    queryKey: dashboardKeys.countriesMedalists(),
    queryFn: dashboardApi.countriesMedalists,
  })
}
