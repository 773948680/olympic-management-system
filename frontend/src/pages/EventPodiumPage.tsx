import { Link, useParams } from 'react-router-dom'
import { ArrowLeft, ListChecks, Medal } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { PageHeader } from '@/components/shared/PageHeader'
import { QueryState } from '@/components/shared/QueryState'
import { EventStatusBadge } from '@/components/events/EventStatusBadge'
import { MedalBadge } from '@/components/results/MedalBadge'
import { medalColors, medalLabels } from '@/components/dashboard/chart-theme'
import { useEvent, useEventPodium, useEventResults } from '@/services/events'
import type { ResultResponse } from '@/types'

const podiumSkeleton = (
  <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
    {Array.from({ length: 3 }).map((_, i) => (
      <Skeleton key={i} className="h-32 w-full" />
    ))}
  </div>
)

const tableSkeleton = (
  <div className="space-y-2 p-4">
    {Array.from({ length: 4 }).map((_, i) => (
      <Skeleton key={i} className="h-10 w-full" />
    ))}
  </div>
)

function formatEventDate(iso: string) {
  return new Date(iso).toLocaleString('fr-FR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

function PodiumTile({ result }: { result: ResultResponse }) {
  const key = result.medal.toLowerCase() as 'gold' | 'silver' | 'bronze'
  const color = medalColors[key]

  return (
    <Card style={{ borderColor: color }} className="border-2">
      <CardContent className="flex flex-col items-center gap-1 py-6 text-center">
        <Medal className="size-8" style={{ color }} />
        <p className="text-xs font-medium tracking-wide text-muted-foreground uppercase">
          {medalLabels[key]} — Position {result.position}
        </p>
        <p className="text-lg font-semibold">
          {result.athleteFirstName} {result.athleteLastName}
        </p>
        {(result.time || result.score !== undefined) && (
          <p className="text-sm text-muted-foreground">
            {result.time ?? (result.score !== null ? `${result.score} pts` : '')}
          </p>
        )}
      </CardContent>
    </Card>
  )
}

export default function EventPodiumPage() {
  const { id } = useParams<{ id: string }>()
  const eventId = id ? Number(id) : undefined

  const event = useEvent(eventId)
  const podium = useEventPodium(eventId)
  const results = useEventResults(eventId)

  return (
    <div>
      <div className="mb-2">
        <Button variant="ghost" size="sm" asChild>
          <Link to="/events">
            <ArrowLeft />
            Retour aux épreuves
          </Link>
        </Button>
      </div>

      <QueryState
        isPending={event.isPending}
        isError={event.isError}
        error={event.error}
        data={event.data}
        onRetry={() => event.refetch()}
        loading={<Skeleton className="mb-6 h-16 w-full" />}
      >
        {(data) => (
          <PageHeader
            title={`Podium — ${data.name}`}
            description={`${data.disciplineName} · ${formatEventDate(data.eventDate)}${data.venue ? ` · ${data.venue}` : ''}`}
            actions={
              <>
                <EventStatusBadge status={data.status} />
                <Button variant="outline" asChild>
                  <Link to={`/results?eventId=${data.id}`}>
                    <ListChecks />
                    Gérer les résultats
                  </Link>
                </Button>
              </>
            }
          />
        )}
      </QueryState>

      <QueryState
        isPending={podium.isPending}
        isError={podium.isError}
        error={podium.error}
        data={podium.data}
        isEmpty={(d) => d.length === 0}
        emptyMessage="Aucun podium disponible : aucun résultat médaillé n'a encore été enregistré pour cette épreuve."
        onRetry={() => podium.refetch()}
        loading={podiumSkeleton}
      >
        {(data) => (
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
            {data.map((result) => (
              <PodiumTile key={result.id} result={result} />
            ))}
          </div>
        )}
      </QueryState>

      <Card className="mt-6 py-0">
        <CardHeader className="py-4">
          <CardTitle>Tous les résultats</CardTitle>
          <CardDescription>Classement complet de l'épreuve, trié par position</CardDescription>
        </CardHeader>
        <CardContent className="px-0">
          <QueryState
            isPending={results.isPending}
            isError={results.isError}
            error={results.error}
            data={results.data}
            isEmpty={(d) => d.length === 0}
            emptyMessage="Aucun résultat enregistré pour cette épreuve."
            onRetry={() => results.refetch()}
            loading={tableSkeleton}
          >
            {(data) => (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Position</TableHead>
                    <TableHead>Athlète</TableHead>
                    <TableHead className="hidden md:table-cell">Temps</TableHead>
                    <TableHead className="hidden md:table-cell">Score</TableHead>
                    <TableHead>Médaille</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {data.map((result) => (
                    <TableRow key={result.id}>
                      <TableCell className="font-medium">{result.position}</TableCell>
                      <TableCell
                        className="max-w-[160px] truncate"
                        title={`${result.athleteFirstName} ${result.athleteLastName}`}
                      >
                        {result.athleteFirstName} {result.athleteLastName}
                      </TableCell>
                      <TableCell className="hidden text-muted-foreground md:table-cell">{result.time || '—'}</TableCell>
                      <TableCell className="hidden text-muted-foreground md:table-cell">{result.score ?? '—'}</TableCell>
                      <TableCell>
                        <MedalBadge medal={result.medal} />
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            )}
          </QueryState>
        </CardContent>
      </Card>
    </div>
  )
}
