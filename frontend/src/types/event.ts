export type EventStatus = 'SCHEDULED' | 'IN_PROGRESS' | 'COMPLETED'

export interface EventResponse {
  id: number
  name: string
  disciplineId: number
  disciplineName: string
  /** Date-heure ISO */
  eventDate: string
  venue?: string
  status: EventStatus
}

export interface EventRequest {
  name: string
  disciplineId: number
  eventDate: string
  venue?: string
  status?: EventStatus
}

export interface EventPatchRequest {
  name?: string
  disciplineId?: number
  eventDate?: string
  venue?: string
  status?: EventStatus
}

export interface EventSearchParams {
  disciplineId?: number
  /** Date ISO (yyyy-MM-dd), filtre sur le jour entier */
  date?: string
}
