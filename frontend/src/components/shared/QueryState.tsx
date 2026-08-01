import type { ReactNode } from 'react'
import { AlertCircle, Inbox } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { getErrorMessage } from '@/utils/api-error'

interface QueryStateProps<T> {
  isPending: boolean
  isError: boolean
  error?: unknown
  data: T | undefined
  isEmpty?: (data: T) => boolean
  emptyMessage?: string
  onRetry?: () => void
  loading: ReactNode
  children: (data: T) => ReactNode
}

/**
 * Gère de façon uniforme les 3 états d'une requête TanStack Query
 * (chargement / erreur / vide) pour les cards, graphiques et tableaux du
 * dashboard. Aucune donnée fictive n'est jamais rendue : en erreur ou en
 * chargement, `children` (qui contient les vraies données) n'est pas monté.
 */
export function QueryState<T>({
  isPending,
  isError,
  error,
  data,
  isEmpty,
  emptyMessage = 'Aucune donnée disponible.',
  onRetry,
  loading,
  children,
}: QueryStateProps<T>) {
  if (isPending) {
    return <>{loading}</>
  }

  if (isError) {
    return (
      <div className="flex flex-col items-center justify-center gap-2 px-4 py-10 text-center">
        <AlertCircle className="size-8 text-destructive" />
        <p className="text-sm font-medium text-destructive">Impossible de charger les données</p>
        <p className="text-xs text-muted-foreground">{getErrorMessage(error)}</p>
        {onRetry ? (
          <Button variant="outline" size="sm" onClick={onRetry} className="mt-2">
            Réessayer
          </Button>
        ) : null}
      </div>
    )
  }

  if (data === undefined || (isEmpty ? isEmpty(data) : false)) {
    return (
      <div className="flex flex-col items-center justify-center gap-2 px-4 py-10 text-center">
        <Inbox className="size-8 text-muted-foreground" />
        <p className="text-sm text-muted-foreground">{emptyMessage}</p>
      </div>
    )
  }

  return <>{children(data)}</>
}
