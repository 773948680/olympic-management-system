import { useQuery } from '@tanstack/react-query'
import { athletesApi, dashboardApi, disciplinesApi, eventsApi } from '@/api'
import type { EventStatus } from '@/types'

export interface DisciplineAthleteCount {
  disciplineId: number
  name: string
  count: number
}

export interface StatisticsOverview {
  disciplinesCount: number
  eventsCount: number
  athletesCount: number
  countriesCount: number
  eventsByStatus: Record<EventStatus, number>
  genderCounts: { male: number; female: number }
  athletesByDiscipline: DisciplineAthleteCount[]
}

const EVENTS_SAMPLE_SIZE = 200

async function fetchStatisticsOverview(): Promise<StatisticsOverview> {
  const [disciplinesPage, athletes, countries, eventsPage, malePage, femalePage] = await Promise.all([
    disciplinesApi.list({ size: 100 }),
    dashboardApi.athletesCount(),
    dashboardApi.countriesCount(),
    eventsApi.search({ size: EVENTS_SAMPLE_SIZE }),
    athletesApi.search({ gender: 'MALE', size: 1 }),
    athletesApi.search({ gender: 'FEMALE', size: 1 }),
  ])

  const eventsByStatus: Record<EventStatus, number> = {
    SCHEDULED: 0,
    IN_PROGRESS: 0,
    COMPLETED: 0,
  }
  for (const event of eventsPage.content) {
    eventsByStatus[event.status] += 1
  }

  const athletesByDiscipline = await Promise.all(
    disciplinesPage.content.map(async (discipline) => {
      const page = await athletesApi.search({ disciplineId: discipline.id, size: 1 })
      return { disciplineId: discipline.id, name: discipline.name, count: page.totalElements }
    }),
  )

  return {
    disciplinesCount: disciplinesPage.totalElements,
    eventsCount: eventsPage.totalElements,
    athletesCount: athletes.count,
    countriesCount: countries.count,
    eventsByStatus,
    genderCounts: { male: malePage.totalElements, female: femalePage.totalElements },
    athletesByDiscipline,
  }
}

export function useStatisticsOverview() {
  return useQuery({
    queryKey: ['statistics', 'overview'],
    queryFn: fetchStatisticsOverview,
  })
}
