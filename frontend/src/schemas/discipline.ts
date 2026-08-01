import { z } from 'zod'

export const disciplineFormSchema = z.object({
  name: z
    .string()
    .trim()
    .min(1, "Le nom de la discipline est obligatoire")
    .max(100, 'Le nom ne peut pas dépasser 100 caractères'),
  description: z
    .string()
    .trim()
    .max(500, 'La description ne peut pas dépasser 500 caractères')
    .optional()
    .or(z.literal('')),
})

export type DisciplineFormValues = z.infer<typeof disciplineFormSchema>
