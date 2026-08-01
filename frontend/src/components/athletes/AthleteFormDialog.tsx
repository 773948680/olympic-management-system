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
import { athleteFormSchema, genderValues, type AthleteFormValues } from '@/schemas/athlete'
import { useCreateAthlete, useUpdateAthlete } from '@/services/athletes'
import { useDisciplines } from '@/services/disciplines'
import { getErrorMessage } from '@/utils/api-error'
import { genderLabels } from './gender-labels'
import type { AthleteResponse } from '@/types'

interface AthleteFormDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  athlete?: AthleteResponse
}

export function AthleteFormDialog({ open, onOpenChange, athlete }: AthleteFormDialogProps) {
  const isEdit = !!athlete
  const disciplines = useDisciplines({ size: 100 })
  const createAthlete = useCreateAthlete()
  const updateAthlete = useUpdateAthlete()
  const isPending = createAthlete.isPending || updateAthlete.isPending

  const form = useForm<AthleteFormValues>({
    resolver: zodResolver(athleteFormSchema),
    defaultValues: {
      firstName: '', lastName: '', gender: 'MALE', dateOfBirth: '',
      nationality: '', disciplineId: '', height: '', weight: '',
    },
  })

  useEffect(() => {
    if (open) {
      form.reset({
        firstName: athlete?.firstName ?? '',
        lastName: athlete?.lastName ?? '',
        gender: athlete?.gender ?? 'MALE',
        dateOfBirth: athlete?.dateOfBirth ?? '',
        nationality: athlete?.nationality ?? '',
        disciplineId: athlete ? String(athlete.disciplineId) : '',
        height: athlete ? String(athlete.height) : '',
        weight: athlete ? String(athlete.weight) : '',
      })
    }
  }, [open, athlete, form])

  function onSubmit(values: AthleteFormValues) {
    const payload = {
      firstName: values.firstName,
      lastName: values.lastName,
      gender: values.gender,
      dateOfBirth: values.dateOfBirth,
      nationality: values.nationality,
      disciplineId: Number(values.disciplineId),
      height: Number(values.height),
      weight: Number(values.weight),
    }

    const mutation = isEdit
      ? updateAthlete.mutateAsync({ id: athlete.id, data: payload })
      : createAthlete.mutateAsync(payload)

    mutation
      .then(() => {
        toast.success(isEdit ? 'Athlète modifié.' : 'Athlète créé.')
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
          <DialogTitle>{isEdit ? "Modifier l'athlète" : 'Nouvel athlète'}</DialogTitle>
          <DialogDescription>
            {isEdit ? "Mettez à jour les informations de cet athlète." : "Renseignez les informations du nouvel athlète."}
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="firstName"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Prénom</FormLabel>
                    <FormControl>
                      <Input placeholder="Usain" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="lastName"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Nom</FormLabel>
                    <FormControl>
                      <Input placeholder="Bolt" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="gender"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Genre</FormLabel>
                    <Select value={field.value} onValueChange={field.onChange}>
                      <FormControl>
                        <SelectTrigger className="w-full">
                          <SelectValue />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        {genderValues.map((g) => (
                          <SelectItem key={g} value={g}>
                            {genderLabels[g]}
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
                name="dateOfBirth"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Date de naissance</FormLabel>
                    <FormControl>
                      <Input type="date" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            <FormField
              control={form.control}
              name="nationality"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Nationalité</FormLabel>
                  <FormControl>
                    <Input placeholder="Jamaïque" {...field} />
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

            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="height"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Taille (cm)</FormLabel>
                    <FormControl>
                      <Input type="number" min={100} max={250} {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="weight"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Poids (kg)</FormLabel>
                    <FormControl>
                      <Input type="number" min={20} max={300} step="0.1" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

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
