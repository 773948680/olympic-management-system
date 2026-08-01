export interface DisciplineResponse {
  id: number
  name: string
  description?: string
}

export interface DisciplineRequest {
  name: string
  description?: string
}

export interface DisciplinePatchRequest {
  name?: string
  description?: string
}
