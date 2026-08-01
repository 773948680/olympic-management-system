import { z } from 'zod'

export const genderValues = ['MALE', 'FEMALE'] as const

function isPastDate(value: string) {
  const date = new Date(value)
  return date.getTime() < Date.now()
}

export const athleteFormSchema = z.object({
  firstName: z.string().trim().min(1, 'Le prénom est obligatoire').max(100, 'Le prénom ne peut pas dépasser 100 caractères'),
  lastName: z.string().trim().min(1, 'Le nom est obligatoire').max(100, 'Le nom ne peut pas dépasser 100 caractères'),
  gender: z.enum(genderValues),
  dateOfBirth: z
    .string()
    .min(1, 'La date de naissance est obligatoire')
    .refine(isPastDate, { message: 'La date de naissance doit être dans le passé' }),
  nationality: z.string().trim().min(1, 'La nationalité est obligatoire').max(100, 'La nationalité ne peut pas dépasser 100 caractères'),
  disciplineId: z.string().min(1, 'La discipline est obligatoire'),
  height: z
    .string()
    .min(1, 'La taille est obligatoire')
    .refine((v) => Number.isInteger(Number(v)) && Number(v) >= 100 && Number(v) <= 250, {
      message: 'La taille doit être réaliste (100 à 250 cm)',
    }),
  weight: z
    .string()
    .min(1, 'Le poids est obligatoire')
    .refine((v) => Number.isFinite(Number(v)) && Number(v) >= 20 && Number(v) <= 300, {
      message: 'Le poids doit être réaliste (20 à 300 kg)',
    }),
})

export type AthleteFormValues = z.infer<typeof athleteFormSchema>
