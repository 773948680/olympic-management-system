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
import { resultFormSchema, type ResultFormValues } from '@/schemas/result'
import { useCreateResult, useUpdateResult } from '@/services/results'
import { useAthletes } from '@/services/athletes'
import { getErrorMessage } from '@/utils/api-error'
import type { ResultResponse } from '@/types'

interface ResultFormDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  eventId: number
  disciplineId?: number
  result?: ResultResponse
}

export function ResultFormDialog({ open, onOpenChange, eventId, disciplineId, result }: ResultFormDialogProps) {
  const isEdit = !!result
  const athletes = useAthletes({ disciplineId, size: 100 })
  const createResult = useCreateResult(eventId)
  const updateResult = useUpdateResult(eventId)
  const isPending = createResult.isPending || updateResult.isPending

  const form = useForm<ResultFormValues>({
    resolver: zodResolver(resultFormSchema),
    defaultValues: { athleteId: '', position: '', time: '', score: '' },
  })

  useEffect(() => {
    if (open) {
      form.reset({
        athleteId: result ? String(result.athleteId) : '',
        position: result ? String(result.position) : '',
        time: result?.time ?? '',
        score: result?.score !== undefined && result?.score !== null ? String(result.score) : '',
      })
    }
  }, [open, result, form])

  function onSubmit(values: ResultFormValues) {
    const payload = {
      eventId,
      athleteId: Number(values.athleteId),
      position: Number(values.position),
      time: values.time || undefined,
      score: values.score ? Number(values.score) : undefined,
    }

    const mutation = isEdit
      ? updateResult.mutateAsync({ id: result.id, data: payload })
      : createResult.mutateAsync(payload)

    mutation
      .then(() => {
        toast.success(isEdit ? 'Résultat modifié.' : 'Résultat enregistré.')
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
          <DialogTitle>{isEdit ? 'Modifier le résultat' : 'Nouveau résultat'}</DialogTitle>
          <DialogDescription>
            La médaille est attribuée automatiquement selon la position (1 = Or, 2 = Argent, 3 = Bronze).
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="athleteId"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Athlète</FormLabel>
                  <Select value={field.value} onValueChange={field.onChange}>
                    <FormControl>
                      <SelectTrigger className="w-full">
                        <SelectValue placeholder="Sélectionner un athlète" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      {athletes.data?.content.map((a) => (
                        <SelectItem key={a.id} value={String(a.id)}>
                          {a.firstName} {a.lastName} — {a.nationality}
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
              name="position"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Position</FormLabel>
                  <FormControl>
                    <Input type="number" min={1} placeholder="1" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="time"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Temps</FormLabel>
                  <FormControl>
                    <Input placeholder="9.58s (optionnel)" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="score"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Score</FormLabel>
                  <FormControl>
                    <Input type="number" min={0} step="0.01" placeholder="Optionnel" {...field} />
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
