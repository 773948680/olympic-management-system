export type MedalType = 'GOLD' | 'SILVER' | 'BRONZE' | 'NONE'

export interface ResultResponse {
  id: number
  eventId: number
  eventName: string
  athleteId: number
  athleteFirstName: string
  athleteLastName: string
  position: number
  time?: string
  score?: number
  medal: MedalType
}

export interface ResultRequest {
  eventId: number
  athleteId: number
  position: number
  time?: string
  score?: number
}
