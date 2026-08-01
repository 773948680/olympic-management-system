import { z } from 'zod'

export const loginFormSchema = z.object({
  username: z.string().trim().min(1, "Le nom d'utilisateur est obligatoire"),
  password: z.string().min(1, 'Le mot de passe est obligatoire'),
})

export type LoginFormValues = z.infer<typeof loginFormSchema>
