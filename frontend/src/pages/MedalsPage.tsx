import { useMemo, useState } from 'react'
import { Search } from 'lucide-react'
import { Card, CardContent } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Skeleton } from '@/components/ui/skeleton'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { PageHeader } from '@/components/shared/PageHeader'
import { QueryState } from '@/components/shared/QueryState'
import { medalColors, medalLabels } from '@/components/dashboard/chart-theme'
import { useMedalTable } from '@/services/medals'

const tableSkeleton = (
  <div className="space-y-2 p-4">
    {Array.from({ length: 8 }).map((_, i) => (
      <Skeleton key={i} className="h-10 w-full" />
    ))}
  </div>
)

function MedalHeader({ color, label }: { color: string; label: string }) {
  return (
    <span className="inline-flex items-center justify-end gap-1.5">
      <span className="size-2 rounded-full" style={{ backgroundColor: color }} aria-hidden />
      {label}
    </span>
  )
}

export default function MedalsPage() {
  const medalTable = useMedalTable()
  const [search, setSearch] = useState('')

  const filtered = useMemo(() => {
    const data = medalTable.data ?? []
    if (!search.trim()) return data
    const q = search.trim().toLowerCase()
    return data.filter((row) => row.nationality.toLowerCase().includes(q))
  }, [medalTable.data, search])

  return (
    <div>
      <PageHeader
        title="Tableau des médailles"
        description="Classement officiel des nations : Or, puis Argent, puis Bronze"
      />

      <div className="mb-4">
        <div className="relative w-72">
          <Search className="pointer-events-none absolute top-1/2 left-2.5 size-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            placeholder="Rechercher une nation…"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="pl-8"
          />
        </div>
      </div>

      <Card className="py-0">
        <CardContent className="px-0">
          <QueryState
            isPending={medalTable.isPending}
            isError={medalTable.isError}
            error={medalTable.error}
            data={medalTable.data}
            isEmpty={() => filtered.length === 0}
            emptyMessage={
              search
                ? `Aucune nation ne correspond à "${search}".`
                : "Aucune médaille n'a encore été attribuée."
            }
            onRetry={() => medalTable.refetch()}
            loading={tableSkeleton}
          >
            {() => (
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
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {filtered.map((row, index) => (
                    <TableRow key={row.nationality}>
                      <TableCell className="text-muted-foreground tabular-nums">{index + 1}</TableCell>
                      <TableCell className="max-w-[160px] truncate font-medium" title={row.nationality}>
                        {row.nationality}
                      </TableCell>
                      <TableCell className="text-right tabular-nums">{row.gold}</TableCell>
                      <TableCell className="text-right tabular-nums">{row.silver}</TableCell>
                      <TableCell className="text-right tabular-nums">{row.bronze}</TableCell>
                      <TableCell className="text-right font-semibold tabular-nums">{row.total}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            )}
          </QueryState>
        </CardContent>
      </Card>
    </div>
  )
}
