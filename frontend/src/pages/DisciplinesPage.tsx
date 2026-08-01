import { useState } from 'react'
import { Pencil, Plus, Trash2 } from 'lucide-react'
import { toast } from 'sonner'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
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
import { DataPagination } from '@/components/shared/DataPagination'
import { ConfirmDeleteDialog } from '@/components/shared/ConfirmDeleteDialog'
import { DisciplineFormDialog } from '@/components/disciplines/DisciplineFormDialog'
import { useDeleteDiscipline, useDisciplines } from '@/services/disciplines'
import { useClampPage } from '@/hooks/useClampPage'
import { getErrorMessage } from '@/utils/api-error'
import type { DisciplineResponse } from '@/types'

const PAGE_SIZE = 10

const tableSkeleton = (
  <div className="space-y-2 p-4">
    {Array.from({ length: 5 }).map((_, i) => (
      <Skeleton key={i} className="h-10 w-full" />
    ))}
  </div>
)

export default function DisciplinesPage() {
  const [page, setPage] = useState(0)
  const disciplines = useDisciplines({ page, size: PAGE_SIZE })
  const deleteDiscipline = useDeleteDiscipline()
  useClampPage(disciplines.data?.totalPages, page, setPage)

  const [formOpen, setFormOpen] = useState(false)
  const [editing, setEditing] = useState<DisciplineResponse | undefined>(undefined)
  const [deleting, setDeleting] = useState<DisciplineResponse | undefined>(undefined)

  function openCreate() {
    setEditing(undefined)
    setFormOpen(true)
  }

  function openEdit(discipline: DisciplineResponse) {
    setEditing(discipline)
    setFormOpen(true)
  }

  function confirmDelete() {
    if (!deleting) return
    deleteDiscipline.mutate(deleting.id, {
      onSuccess: () => {
        toast.success('Discipline supprimée.')
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
        title="Disciplines"
        description="Gestion des disciplines sportives"
        actions={
          <Button onClick={openCreate}>
            <Plus />
            Nouvelle discipline
          </Button>
        }
      />

      <Card className="py-0">
        <CardContent className="px-0">
          <QueryState
            isPending={disciplines.isPending}
            isError={disciplines.isError}
            error={disciplines.error}
            data={disciplines.data}
            isEmpty={(d) => d.content.length === 0}
            emptyMessage="Aucune discipline enregistrée. Créez la première discipline pour commencer."
            onRetry={() => disciplines.refetch()}
            loading={tableSkeleton}
          >
            {(data) => (
              <>
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Nom</TableHead>
                      <TableHead className="hidden md:table-cell">Description</TableHead>
                      <TableHead className="w-24 text-right">Actions</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {data.content.map((discipline) => (
                      <TableRow key={discipline.id}>
                        <TableCell className="max-w-[200px] truncate font-medium" title={discipline.name}>
                          {discipline.name}
                        </TableCell>
                        <TableCell className="hidden max-w-md truncate text-muted-foreground md:table-cell">
                          {discipline.description || '—'}
                        </TableCell>
                        <TableCell className="text-right">
                          <Button
                            variant="ghost"
                            size="icon-sm"
                            aria-label={`Modifier ${discipline.name}`}
                            onClick={() => openEdit(discipline)}
                          >
                            <Pencil />
                          </Button>
                          <Button
                            variant="ghost"
                            size="icon-sm"
                            aria-label={`Supprimer ${discipline.name}`}
                            onClick={() => setDeleting(discipline)}
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

      <DisciplineFormDialog open={formOpen} onOpenChange={setFormOpen} discipline={editing} />

      <ConfirmDeleteDialog
        open={!!deleting}
        onOpenChange={(open) => !open && setDeleting(undefined)}
        title="Supprimer cette discipline ?"
        description={
          deleting
            ? `"${deleting.name}" sera définitivement supprimée. Cette action est impossible si des athlètes ou épreuves y sont encore rattachés.`
            : ''
        }
        onConfirm={confirmDelete}
        isPending={deleteDiscipline.isPending}
      />
    </div>
  )
}
