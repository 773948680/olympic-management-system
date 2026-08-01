import { useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { Inbox, Pencil, Plus, Trash2 } from 'lucide-react'
import { toast } from 'sonner'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
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
import { ConfirmDeleteDialog } from '@/components/shared/ConfirmDeleteDialog'
import { ResultFormDialog } from '@/components/results/ResultFormDialog'
import { MedalBadge } from '@/components/results/MedalBadge'
import { useEvents, useEventResults } from '@/services/events'
import { useDeleteResult } from '@/services/results'
import { getErrorMessage } from '@/utils/api-error'
import type { ResultResponse } from '@/types'

const tableSkeleton = (
  <div className="space-y-2 p-4">
    {Array.from({ length: 5 }).map((_, i) => (
      <Skeleton key={i} className="h-10 w-full" />
    ))}
  </div>
)

export default function ResultsPage() {
  const events = useEvents({ size: 100 })
  const [searchParams] = useSearchParams()
  const eventIdParam = searchParams.get('eventId')
  const [eventId, setEventId] = useState<number | undefined>(
    eventIdParam ? Number(eventIdParam) : undefined,
  )
  const selectedEvent = events.data?.content.find((e) => e.id === eventId)

  const results = useEventResults(eventId)
  const deleteResult = useDeleteResult(eventId ?? 0)

  const [formOpen, setFormOpen] = useState(false)
  const [editing, setEditing] = useState<ResultResponse | undefined>(undefined)
  const [deleting, setDeleting] = useState<ResultResponse | undefined>(undefined)

  function openCreate() {
    setEditing(undefined)
    setFormOpen(true)
  }

  function openEdit(result: ResultResponse) {
    setEditing(result)
    setFormOpen(true)
  }

  function confirmDelete() {
    if (!deleting) return
    deleteResult.mutate(deleting.id, {
      onSuccess: () => {
        toast.success('Résultat supprimé.')
        setDeleting(undefined)
      },
      onError: (error) => {
        toast.error(getErrorMessage(error))
        setDeleting(undefined)
      },
    })
  }

  return (
    <div>
      <PageHeader
        title="Résultats"
        description="Enregistrement des résultats et attribution automatique des médailles"
        actions={
          eventId ? (
            <Button onClick={openCreate}>
              <Plus />
              Nouveau résultat
            </Button>
          ) : undefined
        }
      />

      <div className="mb-4">
        <Select
          value={eventId ? String(eventId) : ''}
          onValueChange={(value) => setEventId(Number(value))}
        >
          <SelectTrigger className="w-96">
            <SelectValue placeholder="Sélectionner une épreuve" />
          </SelectTrigger>
          <SelectContent>
            {events.data?.content.map((e) => (
              <SelectItem key={e.id} value={String(e.id)}>
                {e.name} — {e.disciplineName}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      {!eventId ? (
        <Card>
          <CardContent className="flex flex-col items-center gap-2 py-16 text-center text-sm text-muted-foreground">
            <Inbox className="size-8 text-muted-foreground/60" />
            Sélectionnez une épreuve ci-dessus pour consulter et gérer ses résultats.
          </CardContent>
        </Card>
      ) : (
        <Card className="py-0">
          <CardContent className="px-0">
            <QueryState
              isPending={results.isPending}
              isError={results.isError}
              error={results.error}
              data={results.data}
              isEmpty={(d) => d.length === 0}
              emptyMessage="Aucun résultat enregistré pour cette épreuve."
              onRetry={() => results.refetch()}
              loading={tableSkeleton}
            >
              {(data) => (
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Position</TableHead>
                      <TableHead>Athlète</TableHead>
                      <TableHead className="hidden md:table-cell">Temps</TableHead>
                      <TableHead className="hidden md:table-cell">Score</TableHead>
                      <TableHead>Médaille</TableHead>
                      <TableHead className="w-24 text-right">Actions</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {data.map((result) => (
                      <TableRow key={result.id}>
                        <TableCell className="font-medium">{result.position}</TableCell>
                        <TableCell
                          className="max-w-[140px] truncate"
                          title={`${result.athleteFirstName} ${result.athleteLastName}`}
                        >
                          {result.athleteFirstName} {result.athleteLastName}
                        </TableCell>
                        <TableCell className="hidden text-muted-foreground md:table-cell">{result.time || '—'}</TableCell>
                        <TableCell className="hidden text-muted-foreground md:table-cell">{result.score ?? '—'}</TableCell>
                        <TableCell>
                          <MedalBadge medal={result.medal} />
                        </TableCell>
                        <TableCell className="text-right">
                          <Button
                            variant="ghost"
                            size="icon-sm"
                            aria-label={`Modifier le résultat de ${result.athleteFirstName} ${result.athleteLastName}`}
                            onClick={() => openEdit(result)}
                          >
                            <Pencil />
                          </Button>
                          <Button
                            variant="ghost"
                            size="icon-sm"
                            aria-label={`Supprimer le résultat de ${result.athleteFirstName} ${result.athleteLastName}`}
                            onClick={() => setDeleting(result)}
                          >
                            <Trash2 className="text-destructive" />
                          </Button>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              )}
            </QueryState>
          </CardContent>
        </Card>
      )}

      {eventId && (
        <ResultFormDialog
          open={formOpen}
          onOpenChange={setFormOpen}
          eventId={eventId}
          disciplineId={selectedEvent?.disciplineId}
          result={editing}
        />
      )}

      <ConfirmDeleteDialog
        open={!!deleting}
        onOpenChange={(open) => !open && setDeleting(undefined)}
        title="Supprimer ce résultat ?"
        description={
          deleting
            ? `Le résultat de ${deleting.athleteFirstName} ${deleting.athleteLastName} (position ${deleting.position}) sera définitivement supprimé. Les médailles seront recalculées.`
            : ''
        }
        onConfirm={confirmDelete}
        isPending={deleteResult.isPending}
      />
    </div>
  )
}
