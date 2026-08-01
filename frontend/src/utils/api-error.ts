import axios from 'axios'

/**
 * Forme exacte des erreurs renvoyées par GlobalExceptionHandler (backend) :
 * RFC 7807 ProblemDetail, avec une propriété `errors` additionnelle pour les
 * erreurs de validation (400).
 */
export interface ProblemDetail {
  type?: string
  title?: string
  status: number
  detail?: string
  instance?: string
  errors?: Record<string, string>
}

export function getErrorMessage(error: unknown): string {
  if (axios.isAxiosError<ProblemDetail>(error)) {
    return error.response?.data?.detail ?? error.message
  }
  if (error instanceof Error) {
    return error.message
  }
  return 'Une erreur inattendue est survenue.'
}

export function getFieldErrors(error: unknown): Record<string, string> | undefined {
  if (axios.isAxiosError<ProblemDetail>(error)) {
    return error.response?.data?.errors
  }
  return undefined
}
