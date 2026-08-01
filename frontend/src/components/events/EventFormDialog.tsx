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
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { eventFormSchema, eventStatusValues, type EventFormValues } from '@/schemas/event'
import { useCreateEvent, useUpdateEvent } from '@/services/events'
import { useDisciplines } from '@/services/disciplines'
import { getErrorMessage } from '@/utils/api-error'
import { eventStatusLabels } from './event-status'
import type { EventResponse } from '@/types'

interface EventFormDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  event?: EventResponse
}

function toDatetimeLocalValue(iso: string) {
  return iso.length >= 16 ? iso.slice(0, 16) : iso
}

export function EventFormDialog({ open, onOpenChange, event }: EventFormDialogProps) {
  const isEdit = !!event
  const disciplines = useDisciplines({ size: 100 })
  const createEvent = useCreateEvent()
  const updateEvent = useUpdateEvent()
  const isPending = createEvent.isPending || updateEvent.isPending

  const form = useForm<EventFormValues>({
    resolver: zodResolver(eventFormSchema),
    defaultValues: { name: '', disciplineId: '', eventDate: '', venue: '', status: 'SCHEDULED' },
  })

  useEffect(() => {
    if (open) {
      form.reset({
        name: event?.name ?? '',
        disciplineId: event ? String(event.disciplineId) : '',
        eventDate: event ? toDatetimeLocalValue(event.eventDate) : '',
        venue: event?.venue ?? '',
        status: event?.status ?? 'SCHEDULED',
      })
    }
  }, [open, event, form])

  function onSubmit(values: EventFormValues) {
    const payload = {
      name: values.name,
      disciplineId: Number(values.disciplineId),
      eventDate: values.eventDate,
      venue: values.venue || undefined,
      status: values.status,
    }

    const mutation = isEdit
      ? updateEvent.mutateAsync({ id: event.id, data: payload })
      : createEvent.mutateAsync(payload)

    mutation
      .then(() => {
        toast.success(isEdit ? 'Épreuve modifiée.' : 'Épreuve créée.')
        onOpenChange(false)
      })
      .catch((error: unknown) => {
        toast.error(getErrorMessage(error))
      })
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{isEdit ? "Modifier l'épreuve" : 'Nouvelle épreuve'}</DialogTitle>
          <DialogDescription>
            {isEdit ? 'Mettez à jour les informations de cette épreuve.' : 'Renseignez les informations de la nouvelle épreuve.'}
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
                    <Input placeholder="100m Finale" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="disciplineId"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Discipline</FormLabel>
                  <Select value={field.value} onValueChange={field.onChange}>
                    <FormControl>
                      <SelectTrigger className="w-full">
                        <SelectValue placeholder="Sélectionner une discipline" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      {disciplines.data?.content.map((d) => (
                        <SelectItem key={d.id} value={String(d.id)}>
                          {d.name}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="eventDate"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Date et heure</FormLabel>
                  <FormControl>
                    <Input type="datetime-local" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="venue"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Lieu</FormLabel>
                  <FormControl>
                    <Input placeholder="Stade de Dakar" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="status"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Statut</FormLabel>
                  <Select value={field.value} onValueChange={field.onChange}>
                    <FormControl>
                      <SelectTrigger className="w-full">
                        <SelectValue />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      {eventStatusValues.map((status) => (
                        <SelectItem key={status} value={status}>
                          {eventStatusLabels[status]}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
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
