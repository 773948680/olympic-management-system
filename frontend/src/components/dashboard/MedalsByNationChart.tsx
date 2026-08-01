import { Bar, BarChart, CartesianGrid, Legend, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import type { MedalTableEntry } from '@/types'
import { chartText, medalColors, medalLabels } from './chart-theme'
import { ChartTooltip } from './ChartTooltip'

interface MedalsByNationChartProps {
  data: MedalTableEntry[]
}

const TOP_N = 8
const GAP = chartText.surface

const LEGEND_ITEMS = [
  { label: medalLabels.gold, color: medalColors.gold },
  { label: medalLabels.silver, color: medalColors.silver },
  { label: medalLabels.bronze, color: medalColors.bronze },
]

function MedalLegend() {
  return (
    <ul className="mt-2 flex flex-wrap items-center justify-center gap-4">
      {LEGEND_ITEMS.map((item) => (
        <li key={item.label} className="flex items-center gap-1.5">
          <span className="h-0.5 w-3.5 shrink-0" style={{ backgroundColor: item.color }} />
          <span className="text-xs text-muted-foreground">{item.label}</span>
        </li>
      ))}
    </ul>
  )
}

export function MedalsByNationChart({ data }: MedalsByNationChartProps) {
  const chartData = [...data]
    .sort((a, b) => b.total - a.total)
    .slice(0, TOP_N)

  return (
    <ResponsiveContainer width="100%" height={Math.max(220, chartData.length * 42 + 40)}>
      <BarChart data={chartData} layout="vertical" margin={{ top: 4, right: 16, bottom: 4, left: 4 }}>
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
          dataKey="nationality"
          width={100}
          tick={{ fill: chartText.secondary, fontSize: 12 }}
          axisLine={{ stroke: chartText.axis }}
          tickLine={false}
        />
        <Tooltip content={<ChartTooltip />} cursor={{ fill: 'color-mix(in oklab, currentColor 6%, transparent)' }} />
        <Legend content={<MedalLegend />} />
        <Bar dataKey="gold" name={medalLabels.gold} stackId="medals" fill={medalColors.gold} stroke={GAP} strokeWidth={2} />
        <Bar dataKey="silver" name={medalLabels.silver} stackId="medals" fill={medalColors.silver} stroke={GAP} strokeWidth={2} />
        <Bar
          dataKey="bronze"
          name={medalLabels.bronze}
          stackId="medals"
          fill={medalColors.bronze}
          stroke={GAP}
          strokeWidth={2}
          radius={[0, 4, 4, 0]}
        />
      </BarChart>
    </ResponsiveContainer>
  )
}
