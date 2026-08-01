interface ChartTooltipPayloadItem {
  dataKey?: string | number
  name?: string | number
  value?: string | number | Array<string | number>
  color?: string
}

interface ChartTooltipProps {
  active?: boolean
  label?: string | number
  payload?: ChartTooltipPayloadItem[]
}

/**
 * Tooltip générique : la valeur est mise en avant (gras, encre primaire), le
 * nom de série est secondaire, l'identité de série passe par une "clé ligne"
 * (trait de couleur) plutôt qu'une pastille pleine — cf. dataviz/interaction.md.
 */
export function ChartTooltip({ active, label, payload }: ChartTooltipProps) {
  if (!active || !payload || payload.length === 0) {
    return null
  }

  return (
    <div className="min-w-36 rounded-lg border border-border bg-popover px-3 py-2 shadow-md">
      {label !== undefined ? (
        <p className="mb-1.5 text-xs font-medium text-foreground">{label}</p>
      ) : null}
      <div className="space-y-1">
        {payload.map((entry) => (
          <div key={String(entry.dataKey ?? entry.name)} className="flex items-center gap-2 text-xs">
            <span className="h-0.5 w-3 shrink-0 rounded-full" style={{ backgroundColor: entry.color }} />
            <span className="text-muted-foreground">{entry.name}</span>
            <span className="ml-auto font-semibold tabular-nums text-foreground">
              {String(entry.value)}
            </span>
          </div>
        ))}
      </div>
    </div>
  )
}
