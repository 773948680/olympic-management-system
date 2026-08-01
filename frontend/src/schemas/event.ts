import { z } from 'zod'

export const eventStatusValues = ['SCHEDULED', 'IN_PROGRESS', 'COMPLETED'] as const

export const eventFormSchema = z.object({
  name: z
    .string()
    .trim()
    .min(1, "Le nom de l'épreuve est obligatoire")
    .max(150, 'Le nom ne peut pas dépasser 150 caractères'),
  disciplineId: z.string().min(1, 'La discipline est obligatoire'),
  eventDate: z.string().min(1, "La date de l'épreuve est obligatoire"),
  venue: z.string().trim().max(150, 'Le lieu ne peut pas dépasser 150 caractères').optional().or(z.literal('')),
  status: z.enum(eventStatusValues),
})

export type EventFormValues = z.infer<typeof eventFormSchema>
