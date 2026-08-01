import { Medal } from 'lucide-react'
import { Badge } from '@/components/ui/badge'
import { medalColors, medalLabels } from '@/components/dashboard/chart-theme'
import type { MedalType } from '@/types'

export function MedalBadge({ medal }: { medal: MedalType }) {
  if (medal === 'NONE') {
    return <span className="text-sm text-muted-foreground">—</span>
  }

  const key = medal.toLowerCase() as 'gold' | 'silver' | 'bronze'

  return (
    <Badge
      variant="outline"
      style={{ color: medalColors[key], borderColor: medalColors[key] }}
    >
      <Medal className="size-3" />
      {medalLabels[key]}
    </Badge>
  )
}
