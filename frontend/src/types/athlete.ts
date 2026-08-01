export type Gender = 'MALE' | 'FEMALE'

export interface AthleteResponse {
  id: number
  firstName: string
  lastName: string
  gender: Gender
  /** Date ISO (yyyy-MM-dd) */
  dateOfBirth: string
  nationality: string
  disciplineId: number
  disciplineName: string
  height: number
  weight: number
  /** Date-heure ISO */
  createdAt: string
  updatedAt: string
}

export interface AthleteRequest {
  firstName: string
  lastName: string
  gender: Gender
  dateOfBirth: string
  nationality: string
  disciplineId: number
  height: number
  weight: number
}

export interface AthletePatchRequest {
  firstName?: string
  lastName?: string
  gender?: Gender
  dateOfBirth?: string
  nationality?: string
  disciplineId?: number
  height?: number
  weight?: number
}

export interface AthleteSearchParams {
  lastName?: string
  firstName?: string
  gender?: Gender
  nationality?: string
  disciplineId?: number
  bornAfter?: string
  bornBefore?: string
}
