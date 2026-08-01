import { z } from 'zod'

export const resultFormSchema = z.object({
  athleteId: z.string().min(1, "L'athlète est obligatoire"),
  position: z
    .string()
    .min(1, 'La position est obligatoire')
    .refine((v) => Number.isInteger(Number(v)) && Number(v) >= 1, {
      message: 'La position doit être un entier supérieur ou égal à 1',
    }),
  time: z.string().trim().max(50, 'Le temps ne peut pas dépasser 50 caractères').optional().or(z.literal('')),
  score: z
    .string()
    .optional()
    .or(z.literal(''))
    .refine((v) => !v || (Number.isFinite(Number(v)) && Number(v) >= 0), {
      message: 'Le score doit être positif ou nul',
    }),
})

export type ResultFormValues = z.infer<typeof resultFormSchema>
