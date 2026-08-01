import { useEffect } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { toast } from 'sonner'
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { disciplineFormSchema, type DisciplineFormValues } from '@/schemas/discipline'
import { useCreateDiscipline, useUpdateDiscipline } from '@/services/disciplines'
import { getErrorMessage } from '@/utils/api-error'
import type { DisciplineResponse } from '@/types'

interface DisciplineFormDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  discipline?: DisciplineResponse
}

export function DisciplineFormDialog({ open, onOpenChange, discipline }: DisciplineFormDialogProps) {
  const isEdit = !!discipline
  const createDiscipline = useCreateDiscipline()
  const updateDiscipline = useUpdateDiscipline()
  const isPending = createDiscipline.isPending || updateDiscipline.isPending

  const form = useForm<DisciplineFormValues>({
    resolver: zodResolver(disciplineFormSchema),
    defaultValues: { name: '', description: '' },
  })

  useEffect(() => {
    if (open) {
      form.reset({
        name: discipline?.name ?? '',
        description: discipline?.description ?? '',
      })
    }
  }, [open, discipline, form])

  function onSubmit(values: DisciplineFormValues) {
    const payload = { name: values.name, description: values.description || undefined }

    const mutation = isEdit
      ? updateDiscipline.mutateAsync({ id: discipline.id, data: payload })
      : createDiscipline.mutateAsync(payload)

    mutation
      .then(() => {
        toast.success(isEdit ? 'Discipline modifiée.' : 'Discipline créée.')
        onOpenChange(false)
      })
      .catch((error: unknown) => {
        const message = getErrorMessage(error)
        if (message.toLowerCase().includes('existe déjà')) {
          form.setError('name', { message })
        } else {
          toast.error(message)
        }
      })
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{isEdit ? 'Modifier la discipline' : 'Nouvelle discipline'}</DialogTitle>
          <DialogDescription>
            {isEdit
              ? 'Mettez à jour les informations de cette discipline.'
              : 'Renseignez les informations de la nouvelle discipline.'}
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="name"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Nom</FormLabel>
                  <FormControl>
                    <Input placeholder="Athlétisme" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="description"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Description</FormLabel>
                  <FormControl>
                    <Textarea placeholder="Description de la discipline (optionnel)" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <DialogFooter>
              <Button type="button" variant="outline" onClick={() => onOpenChange(false)} disabled={isPending}>
                Annuler
              </Button>
              <Button type="submit" disabled={isPending}>
                {isPending ? 'Enregistrement…' : isEdit ? 'Enregistrer' : 'Créer'}
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  )
}
