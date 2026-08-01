import { Bar, BarChart, CartesianGrid, LabelList, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import { chartText, magnitudeColor } from '@/components/dashboard/chart-theme'
import { ChartTooltip } from '@/components/dashboard/ChartTooltip'

interface HorizontalRankingChartProps<T extends object> {
  data: T[]
  valueKey: keyof T & string
  labelKey: keyof T & string
  seriesName: string
  /** Nombre max d'entrées affichées, triées par valeur décroissante. Omis = toutes. */
  topN?: number
  yAxisWidth?: number
}

export function HorizontalRankingChart<T extends object>({
  data,
  valueKey,
  labelKey,
  seriesName,
  topN,
  yAxisWidth = 100,
}: HorizontalRankingChartProps<T>) {
  const sorted = [...data].sort((a, b) => Number(b[valueKey]) - Number(a[valueKey]))
  const chartData = topN ? sorted.slice(0, topN) : sorted

  // Recharts v3's TypedDataKey can't resolve `keyof T` through a generic component
  // boundary — the field names themselves are already type-checked via this
  // component's own `valueKey`/`labelKey` props, so this cast is safe.
  const rechartsLabelKey = labelKey as never
  const rechartsValueKey = valueKey as never

  return (
    <ResponsiveContainer width="100%" height={Math.max(220, chartData.length * 42 + 40)}>
      <BarChart data={chartData} layout="vertical" margin={{ top: 4, right: 32, bottom: 4, left: 4 }}>
        <CartesianGrid horizontal={false} stroke={chartText.grid} />
        <XAxis
          type="number"
          allowDecimals={false}
          tick={{ fill: chartText.muted, fontSize: 12 }}
          axisLine={{ stroke: chartText.axis }}
          tickLine={false}
        />
        <YAxis
          type="category"
          dataKey={rechartsLabelKey}
          width={yAxisWidth}
          tick={{ fill: chartText.secondary, fontSize: 12 }}
          axisLine={{ stroke: chartText.axis }}
          tickLine={false}
        />
        <Tooltip content={<ChartTooltip />} cursor={{ fill: 'color-mix(in oklab, currentColor 6%, transparent)' }} />
        <Bar dataKey={rechartsValueKey} name={seriesName} fill={magnitudeColor} radius={[0, 4, 4, 0]}>
          <LabelList dataKey={rechartsValueKey} position="right" style={{ fill: chartText.secondary, fontSize: 12, fontWeight: 600 }} />
        </Bar>
      </BarChart>
    </ResponsiveContainer>
  )
}
