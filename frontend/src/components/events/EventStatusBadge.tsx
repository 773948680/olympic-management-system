import { Badge } from '@/components/ui/badge'
import { eventStatusLabels } from './event-status'
import type { EventStatus } from '@/types'

const variantByStatus: Record<EventStatus, 'secondary' | 'default' | 'outline'> = {
  SCHEDULED: 'secondary',
  IN_PROGRESS: 'default',
  COMPLETED: 'outline',
}

export function EventStatusBadge({ status }: { status: EventStatus }) {
  return <Badge variant={variantByStatus[status]}>{eventStatusLabels[status]}</Badge>
}
