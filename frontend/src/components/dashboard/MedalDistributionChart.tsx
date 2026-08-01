import type { MedalTotalsResponse } from '@/types'
import { medalColors, medalLabels } from './chart-theme'
import { StackedBarBreakdown } from '@/components/shared/StackedBarBreakdown'

interface MedalDistributionChartProps {
  data: MedalTotalsResponse
}

export function MedalDistributionChart({ data }: MedalDistributionChartProps) {
  const { gold, silver, bronze, total } = data
  const segments = [
    { key: 'gold', label: medalLabels.gold, value: gold, color: medalColors.gold },
    { key: 'silver', label: medalLabels.silver, value: silver, color: medalColors.silver },
    { key: 'bronze', label: medalLabels.bronze, value: bronze, color: medalColors.bronze },
  ] as const

  return (
    <StackedBarBreakdown
      segments={segments}
      total={total}
      ariaLabel={`Répartition des médailles : ${gold} or, ${silver} argent, ${bronze} bronze, sur ${total} médailles au total`}
    />
  )
}
