export interface CountResponse {
  count: number
}

export interface MedalTotalsResponse {
  gold: number
  silver: number
  bronze: number
  total: number
}

export interface CountryRankingEntry {
  nationality: string
  gold: number
  silver: number
  bronze: number
  points: number
}

export interface CountryMedalistsEntry {
  nationality: string
  medalists: number
}
