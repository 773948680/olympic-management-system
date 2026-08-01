import type { EventStatus } from '@/types'

export const eventStatusLabels: Record<EventStatus, string> = {
  SCHEDULED: 'Programmée',
  IN_PROGRESS: 'En cours',
  COMPLETED: 'Terminée',
}
