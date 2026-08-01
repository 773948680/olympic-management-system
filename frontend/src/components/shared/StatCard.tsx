import type { LucideIcon } from 'lucide-react'
import { AlertCircle } from 'lucide-react'
import { Card, CardContent } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'

interface StatCardProps {
  label: string
  value: string | number
  icon: LucideIcon
  iconClassName?: string
  loading?: boolean
  error?: boolean
  onRetry?: () => void
}

export function StatCard({ label, value, icon: Icon, iconClassName, loading, error, onRetry }: StatCardProps) {
  return (
    <Card>
      <CardContent className="flex items-center gap-4">
        <div
          className={`flex size-11 shrink-0 items-center justify-center rounded-lg bg-primary/10 text-primary ${iconClassName ?? ''}`}
        >
          <Icon className="size-5" />
        </div>
        <div className="min-w-0">
          <p className="text-sm text-muted-foreground">{label}</p>
          {loading ? (
            <Skeleton className="mt-1 h-7 w-16" />
          ) : error ? (
            <button
              type="button"
              onClick={onRetry}
              disabled={!onRetry}
              className="flex items-center gap-1 text-sm text-destructive enabled:hover:underline"
            >
              <AlertCircle className="size-4" />
              Erreur{onRetry ? ' — réessayer' : ''}
            </button>
          ) : (
            <p className="text-2xl font-semibold tracking-tight">{value}</p>
          )}
        </div>
      </CardContent>
    </Card>
  )
}
