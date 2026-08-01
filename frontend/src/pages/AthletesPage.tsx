import { useState } from 'react'
import { Pencil, Plus, Trash2 } from 'lucide-react'
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
import { AthleteFormDialog } from '@/components/athletes/AthleteFormDialog'
import { genderLabels } from '@/components/athletes/gender-labels'
import { genderValues } from '@/schemas/athlete'
import { useAthletes, useDeleteAthlete } from '@/services/athletes'
import { useDisciplines } from '@/services/disciplines'
import { useClampPage } from '@/hooks/useClampPage'
import { getErrorMessage } from '@/utils/api-error'
import type { AthleteResponse, Gender } from '@/types'

const PAGE_SIZE = 10

const tableSkeleton = (
  <div className="space-y-2 p-4">
    {Array.from({ length: 5 }).map((_, i) => (
      <Skeleton key={i} className="h-10 w-full" />
    ))}
  </div>
)

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString('fr-FR')
}

export default function AthletesPage() {
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const [genderFilter, setGenderFilter] = useState<string>('')
  const [disciplineFilter, setDisciplineFilter] = useState<string>('')

  const disciplines = useDisciplines({ size: 100 })
  const athletes = useAthletes({
    page,
    size: PAGE_SIZE,
    lastName: search || undefined,
    gender: (genderFilter || undefined) as Gender | undefined,
    disciplineId: disciplineFilter ? Number(disciplineFilter) : undefined,
  })
  useClampPage(athletes.data?.totalPages, page, setPage)
  const deleteAthlete = useDeleteAthlete()

  const [formOpen, setFormOpen] = useState(false)
  const [editing, setEditing] = useState<AthleteResponse | undefined>(undefined)
  const [deleting, setDeleting] = useState<AthleteResponse | undefined>(undefined)

  function openCreate() {
    setEditing(undefined)
    setFormOpen(true)
  }

  function openEdit(athlete: AthleteResponse) {
    setEditing(athlete)
    setFormOpen(true)
  }

  function confirmDelete() {
    if (!deleting) return
    deleteAthlete.mutate(deleting.id, {
      onSuccess: () => {
        toast.success('Athlète supprimé.')
        setDeleting(undefined)
      },
      onError: (error) => {
        toast.error(getErrorMessage(error))
        setDeleting(undefined)
      },
    })
  }

  const hasFilters = !!(search || genderFilter || disciplineFilter)

  return (
    <div>
      <PageHeader
        title="Athlètes"
        description="Gestion et recherche multicritère des athlètes"
        actions={
          <Button onClick={openCreate}>
            <Plus />
            Nouvel athlète
          </Button>
        }
      />

      <div className="mb-4 flex flex-wrap gap-3">
        <Input
          placeholder="Rechercher par nom…"
          className="w-56"
          value={search}
          onChange={(e) => {
            setSearch(e.target.value)
            setPage(0)
          }}
        />
        <Select
          value={genderFilter || 'all'}
          onValueChange={(value) => {
            setGenderFilter(value === 'all' ? '' : value)
            setPage(0)
          }}
        >
          <SelectTrigger className="w-40">
            <SelectValue placeholder="Tous genres" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">Tous genres</SelectItem>
            {genderValues.map((g) => (
              <SelectItem key={g} value={g}>
                {genderLabels[g]}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
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
        {hasFilters && (
          <Button
            variant="ghost"
            onClick={() => {
              setSearch('')
              setGenderFilter('')
              setDisciplineFilter('')
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
            isPending={athletes.isPending}
            isError={athletes.isError}
            error={athletes.error}
            data={athletes.data}
            isEmpty={(d) => d.content.length === 0}
            emptyMessage={
              hasFilters
                ? 'Aucun athlète ne correspond à ces critères.'
                : 'Aucun athlète enregistré. Créez le premier athlète pour commencer.'
            }
            onRetry={() => athletes.refetch()}
            loading={tableSkeleton}
          >
            {(data) => (
              <>
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Nom</TableHead>
                      <TableHead className="hidden md:table-cell">Genre</TableHead>
                      <TableHead className="hidden lg:table-cell">Naissance</TableHead>
                      <TableHead>Nationalité</TableHead>
                      <TableHead className="hidden sm:table-cell">Discipline</TableHead>
                      <TableHead className="w-24 text-right">Actions</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {data.content.map((athlete) => (
                      <TableRow key={athlete.id}>
                        <TableCell className="max-w-[140px] truncate font-medium" title={`${athlete.firstName} ${athlete.lastName}`}>
                          {athlete.firstName} {athlete.lastName}
                        </TableCell>
                        <TableCell className="hidden text-muted-foreground md:table-cell">
                          {genderLabels[athlete.gender]}
                        </TableCell>
                        <TableCell className="hidden text-muted-foreground lg:table-cell">
                          {formatDate(athlete.dateOfBirth)}
                        </TableCell>
                        <TableCell className="max-w-[120px] truncate text-muted-foreground" title={athlete.nationality}>
                          {athlete.nationality}
                        </TableCell>
                        <TableCell className="hidden text-muted-foreground sm:table-cell">
                          {athlete.disciplineName}
                        </TableCell>
                        <TableCell className="text-right">
                          <Button
                            variant="ghost"
                            size="icon-sm"
                            aria-label={`Modifier ${athlete.firstName} ${athlete.lastName}`}
                            onClick={() => openEdit(athlete)}
                          >
                            <Pencil />
                          </Button>
                          <Button
                            variant="ghost"
                            size="icon-sm"
                            aria-label={`Supprimer ${athlete.firstName} ${athlete.lastName}`}
                            onClick={() => setDeleting(athlete)}
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

      <AthleteFormDialog open={formOpen} onOpenChange={setFormOpen} athlete={editing} />

      <ConfirmDeleteDialog
        open={!!deleting}
        onOpenChange={(open) => !open && setDeleting(undefined)}
        title="Supprimer cet athlète ?"
        description={
          deleting
            ? `"${deleting.firstName} ${deleting.lastName}" sera définitivement supprimé. Cette action est impossible si des résultats y sont encore rattachés.`
            : ''
        }
        onConfirm={confirmDelete}
        isPending={deleteAthlete.isPending}
      />
    </div>
  )
}
