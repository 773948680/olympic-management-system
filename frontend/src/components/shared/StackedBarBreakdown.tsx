export interface StackedBarSegment {
  key: string
  label: string
  value: number
  color: string
}

interface StackedBarBreakdownProps {
  segments: readonly StackedBarSegment[]
  total: number
  ariaLabel: string
}

/**
 * Répartition part-to-whole d'un seul total → barre empilée unique
 * (jamais un donut/pie, cf. dataviz/anti-patterns.md).
 */
export function StackedBarBreakdown({ segments, total, ariaLabel }: StackedBarBreakdownProps) {
  return (
    <div>
      <div
        className="flex h-8 w-full overflow-hidden rounded-md bg-muted"
        role="img"
        aria-label={ariaLabel}
      >
        {segments.map((segment, index) => {
          const pct = total > 0 ? (segment.value / total) * 100 : 0
          if (pct === 0) return null
          return (
            <div
              key={segment.key}
              style={{
                width: `${pct}%`,
                backgroundColor: segment.color,
                marginRight: index < segments.length - 1 ? 2 : 0,
              }}
              title={`${segment.label} : ${segment.value} (${pct.toFixed(1)} %)`}
            />
          )
        })}
      </div>

      <dl className="mt-4 grid gap-3" style={{ gridTemplateColumns: `repeat(${segments.length}, minmax(0, 1fr))` }}>
        {segments.map((segment) => {
          const pct = total > 0 ? (segment.value / total) * 100 : 0
          return (
            <div key={segment.key} className="flex items-center gap-2">
              <span
                className="size-2.5 shrink-0 rounded-full"
                style={{ backgroundColor: segment.color }}
                aria-hidden
              />
              <div className="min-w-0">
                <dt className="text-xs text-muted-foreground">{segment.label}</dt>
                <dd className="text-sm font-semibold tabular-nums">
                  {segment.value}
                  <span className="ml-1 font-normal text-muted-foreground">({pct.toFixed(0)}%)</span>
                </dd>
              </div>
            </div>
          )
        })}
      </dl>
    </div>
  )
}
