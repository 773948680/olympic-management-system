import { useState } from 'react'
import { Link } from 'react-router-dom'
import { Pencil, Plus, Trash2, Trophy } from 'lucide-react'
import { toast } from 'sonner'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
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
import { DataPagination } from '@/components/shared/DataPagination'
import { ConfirmDeleteDialog } from '@/components/shared/ConfirmDeleteDialog'
import { EventFormDialog } from '@/components/events/EventFormDialog'
import { EventStatusBadge } from '@/components/events/EventStatusBadge'
import { useDeleteEvent, useEvents } from '@/services/events'
import { useDisciplines } from '@/services/disciplines'
import { useClampPage } from '@/hooks/useClampPage'
import { getErrorMessage } from '@/utils/api-error'
import type { EventResponse } from '@/types'

const PAGE_SIZE = 10

const tableSkeleton = (
  <div className="space-y-2 p-4">
    {Array.from({ length: 5 }).map((_, i) => (
      <Skeleton key={i} className="h-10 w-full" />
    ))}
  </div>
)

function formatEventDate(iso: string) {
  return new Date(iso).toLocaleString('fr-FR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

export default function EventsPage() {
  const [page, setPage] = useState(0)
  const [disciplineFilter, setDisciplineFilter] = useState<string>('')
  const [dateFilter, setDateFilter] = useState<string>('')

  const disciplines = useDisciplines({ size: 100 })
  const events = useEvents({
    page,
    size: PAGE_SIZE,
    disciplineId: disciplineFilter ? Number(disciplineFilter) : undefined,
    date: dateFilter || undefined,
  })
  const deleteEvent = useDeleteEvent()
  useClampPage(events.data?.totalPages, page, setPage)

  const [formOpen, setFormOpen] = useState(false)
  const [editing, setEditing] = useState<EventResponse | undefined>(undefined)
  const [deleting, setDeleting] = useState<EventResponse | undefined>(undefined)

  function openCreate() {
    setEditing(undefined)
    setFormOpen(true)
  }

  function openEdit(event: EventResponse) {
    setEditing(event)
    setFormOpen(true)
  }

  function confirmDelete() {
    if (!deleting) return
    deleteEvent.mutate(deleting.id, {
      onSuccess: () => {
        toast.success('Épreuve supprimée.')
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
        title="Épreuves"
        description="Gestion des épreuves"
        actions={
          <Button onClick={openCreate}>
            <Plus />
            Nouvelle épreuve
          </Button>
        }
      />

      <div className="mb-4 flex flex-wrap gap-3">
        <Select
          value={disciplineFilter || 'all'}
          onValueChange={(value) => {
            setDisciplineFilter(value === 'all' ? '' : value)
            setPage(0)
          }}
        >
          <SelectTrigger className="w-56">
            <SelectValue placeholder="Toutes les disciplines" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">Toutes les disciplines</SelectItem>
            {disciplines.data?.content.map((d) => (
              <SelectItem key={d.id} value={String(d.id)}>
                {d.name}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
        <Input
          type="date"
          className="w-44"
          value={dateFilter}
          onChange={(e) => {
            setDateFilter(e.target.value)
            setPage(0)
          }}
        />
        {(disciplineFilter || dateFilter) && (
          <Button
            variant="ghost"
            onClick={() => {
              setDisciplineFilter('')
              setDateFilter('')
              setPage(0)
            }}
          >
            Réinitialiser
          </Button>
        )}
      </div>

      <Card className="py-0">
        <CardContent className="px-0">
          <QueryState
            isPending={events.isPending}
            isError={events.isError}
            error={events.error}
            data={events.data}
            isEmpty={(d) => d.content.length === 0}
            emptyMessage="Aucune épreuve trouvée. Ajustez vos filtres ou créez une nouvelle épreuve."
            onRetry={() => events.refetch()}
            loading={tableSkeleton}
          >
            {(data) => (
              <>
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Nom</TableHead>
                      <TableHead className="hidden md:table-cell">Discipline</TableHead>
                      <TableHead className="hidden sm:table-cell">Date</TableHead>
                      <TableHead className="hidden lg:table-cell">Lieu</TableHead>
                      <TableHead>Statut</TableHead>
                      <TableHead className="w-32 text-right">Actions</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {data.content.map((event) => (
                      <TableRow key={event.id}>
                        <TableCell className="max-w-[130px] truncate font-medium sm:max-w-[160px]" title={event.name}>
                          {event.name}
                        </TableCell>
                        <TableCell className="hidden text-muted-foreground md:table-cell">{event.disciplineName}</TableCell>
                        <TableCell className="hidden text-muted-foreground sm:table-cell">{formatEventDate(event.eventDate)}</TableCell>
                        <TableCell className="hidden text-muted-foreground lg:table-cell">{event.venue || '—'}</TableCell>
                        <TableCell>
                          <EventStatusBadge status={event.status} />
                        </TableCell>
                        <TableCell className="text-right">
                          <Button variant="ghost" size="icon-sm" aria-label={`Podium de ${event.name}`} asChild>
                            <Link to={`/events/${event.id}/podium`}>
                              <Trophy />
                            </Link>
                          </Button>
                          <Button
                            variant="ghost"
                            size="icon-sm"
                            aria-label={`Modifier ${event.name}`}
                            onClick={() => openEdit(event)}
                          >
                            <Pencil />
                          </Button>
                          <Button
                            variant="ghost"
                            size="icon-sm"
                            aria-label={`Supprimer ${event.name}`}
                            onClick={() => setDeleting(event)}
                          >
                            <Trash2 className="text-destructive" />
                          </Button>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
                <DataPagination
                  page={data.page}
                  totalPages={data.totalPages}
                  totalElements={data.totalElements}
                  onPageChange={setPage}
                />
              </>
            )}
          </QueryState>
        </CardContent>
      </Card>

      <EventFormDialog open={formOpen} onOpenChange={setFormOpen} event={editing} />

      <ConfirmDeleteDialog
        open={!!deleting}
        onOpenChange={(open) => !open && setDeleting(undefined)}
        title="Supprimer cette épreuve ?"
        description={
          deleting
            ? `"${deleting.name}" sera définitivement supprimée. Cette action est impossible si des résultats y sont encore rattachés.`
            : ''
        }
        onConfirm={confirmDelete}
        isPending={deleteEvent.isPending}
      />
    </div>
  )
}
