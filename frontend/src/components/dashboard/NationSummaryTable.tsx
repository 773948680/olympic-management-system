import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import type { NationSummaryRow } from '@/utils/nation-summary'
import { medalColors, medalLabels } from './chart-theme'

interface NationSummaryTableProps {
  data: NationSummaryRow[]
}

function MedalHeader({ color, label }: { color: string; label: string }) {
  return (
    <span className="inline-flex items-center justify-end gap-1.5">
      <span className="size-2 rounded-full" style={{ backgroundColor: color }} aria-hidden />
      {label}
    </span>
  )
}

export function NationSummaryTable({ data }: NationSummaryTableProps) {
  return (
    <div className="overflow-x-auto">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead className="w-10">#</TableHead>
            <TableHead>Nation</TableHead>
            <TableHead className="text-right">
              <MedalHeader color={medalColors.gold} label={medalLabels.gold} />
            </TableHead>
            <TableHead className="text-right">
              <MedalHeader color={medalColors.silver} label={medalLabels.silver} />
            </TableHead>
            <TableHead className="text-right">
              <MedalHeader color={medalColors.bronze} label={medalLabels.bronze} />
            </TableHead>
            <TableHead className="text-right">Total</TableHead>
            <TableHead className="hidden text-right md:table-cell">Points</TableHead>
            <TableHead className="hidden text-right md:table-cell">Médaillés</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {data.map((row, index) => (
            <TableRow key={row.nationality}>
              <TableCell className="text-muted-foreground tabular-nums">{index + 1}</TableCell>
              <TableCell className="max-w-[140px] truncate font-medium" title={row.nationality}>
                {row.nationality}
              </TableCell>
              <TableCell className="text-right tabular-nums">{row.gold}</TableCell>
              <TableCell className="text-right tabular-nums">{row.silver}</TableCell>
              <TableCell className="text-right tabular-nums">{row.bronze}</TableCell>
              <TableCell className="text-right font-semibold tabular-nums">{row.total}</TableCell>
              <TableCell className="hidden text-right tabular-nums md:table-cell">{row.points}</TableCell>
              <TableCell className="hidden text-right tabular-nums md:table-cell">{row.medalists}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  )
}
