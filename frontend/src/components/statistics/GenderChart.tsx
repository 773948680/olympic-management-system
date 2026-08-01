import { StackedBarBreakdown } from '@/components/shared/StackedBarBreakdown'

interface GenderChartProps {
  male: number
  female: number
}

// Paire déjà validée (adjacente, CVD-safe) issue de la palette catégorielle
// Or/Argent/Bronze — réutilisée telle quelle, cf. chart-theme.ts.
export function GenderChart({ male, female }: GenderChartProps) {
  const total = male + female
  const segments = [
    { key: 'male', label: 'Hommes', value: male, color: '#2a78d6' },
    { key: 'female', label: 'Femmes', value: female, color: '#eda100' },
  ] as const

  return (
    <StackedBarBreakdown
      segments={segments}
      total={total}
      ariaLabel={`Répartition des athlètes par genre : ${male} hommes, ${female} femmes, sur ${total} athlètes au total`}
    />
  )
}
