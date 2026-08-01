import { StackedBarBreakdown } from '@/components/shared/StackedBarBreakdown'
import type { EventStatus } from '@/types'

interface EventStatusChartProps {
  data: Record<EventStatus, number>
}

// Réutilise la palette catégorielle Or/Argent/Bronze déjà validée (paires
// adjacentes CVD-safe, cf. chart-theme.ts) pour un trio de statuts distinct —
// même ordre de couleurs, mêmes garanties, pas de re-validation nécessaire.
const STATUS_SEGMENTS = [
  { key: 'SCHEDULED', label: 'Programmées', color: '#eda100' },
  { key: 'IN_PROGRESS', label: 'En cours', color: '#2a78d6' },
  { key: 'COMPLETED', label: 'Terminées', color: '#eb6834' },
] as const

export function EventStatusChart({ data }: EventStatusChartProps) {
  const total = data.SCHEDULED + data.IN_PROGRESS + data.COMPLETED
  const segments = STATUS_SEGMENTS.map((s) => ({ ...s, value: data[s.key] }))

  return (
    <StackedBarBreakdown
      segments={segments}
      total={total}
      ariaLabel={`Répartition des épreuves par statut : ${data.SCHEDULED} programmées, ${data.IN_PROGRESS} en cours, ${data.COMPLETED} terminées, sur ${total} épreuves au total`}
    />
  )
}
