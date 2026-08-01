import type { CountryMedalistsEntry, CountryRankingEntry, MedalTableEntry } from '@/types'

export interface NationSummaryRow {
  nationality: string
  gold: number
  silver: number
  bronze: number
  total: number
  points: number
  medalists: number
}

/**
 * Fusionne les 3 réponses backend (medal-table, classement par points,
 * médaillés par pays) en une ligne par nation, pour un tableau récapitulatif
 * unique. Les 3 endpoints partagent normalement le même ensemble de nations
 * (dérivées des mêmes résultats médaillés côté backend) ; la fusion reste
 * défensive au cas où l'un des trois diverge.
 */
export function buildNationSummary(
  medalTable: MedalTableEntry[],
  ranking: CountryRankingEntry[],
  medalists: CountryMedalistsEntry[],
): NationSummaryRow[] {
  const rows = new Map<string, NationSummaryRow>()

  const getOrCreate = (nationality: string): NationSummaryRow => {
    let row = rows.get(nationality)
    if (!row) {
      row = { nationality, gold: 0, silver: 0, bronze: 0, total: 0, points: 0, medalists: 0 }
      rows.set(nationality, row)
    }
    return row
  }

  for (const entry of medalTable) {
    const row = getOrCreate(entry.nationality)
    row.gold = entry.gold
    row.silver = entry.silver
    row.bronze = entry.bronze
    row.total = entry.total
  }
  for (const entry of ranking) {
    getOrCreate(entry.nationality).points = entry.points
  }
  for (const entry of medalists) {
    getOrCreate(entry.nationality).medalists = entry.medalists
  }

  return Array.from(rows.values()).sort(
    (a, b) => b.gold - a.gold || b.silver - a.silver || b.bronze - a.bronze,
  )
}
