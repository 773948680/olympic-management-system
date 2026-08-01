import { Award, CalendarDays, Globe, Users } from 'lucide-react'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { PageHeader } from '@/components/shared/PageHeader'
import { StatCard } from '@/components/shared/StatCard'
import { QueryState } from '@/components/shared/QueryState'
import { EventStatusChart } from '@/components/statistics/EventStatusChart'
import { GenderChart } from '@/components/statistics/GenderChart'
import { HorizontalRankingChart } from '@/components/shared/HorizontalRankingChart'
import { useStatisticsOverview } from '@/services/statistics'

export default function StatisticsPage() {
  const overview = useStatisticsOverview()

  return (
    <div>
      <PageHeader
        title="Statistiques"
        description="Vue d'ensemble de la composition des Jeux Olympiques de Dakar"
      />

      <QueryState
        isPending={overview.isPending}
        isError={overview.isError}
        error={overview.error}
        data={overview.data}
        isEmpty={(d) => d.disciplinesCount === 0 && d.athletesCount === 0 && d.eventsCount === 0}
        emptyMessage="Aucune donnée à afficher pour le moment."
        onRetry={() => overview.refetch()}
        loading={
          <div className="grid grid-cols-2 gap-4 sm:grid-cols-4">
            {Array.from({ length: 4 }).map((_, i) => (
              <Skeleton key={i} className="h-24 w-full" />
            ))}
          </div>
        }
      >
        {(data) => (
          <>
            <div className="grid grid-cols-2 gap-4 sm:grid-cols-4">
              <StatCard label="Disciplines" value={data.disciplinesCount} icon={Award} />
              <StatCard label="Épreuves" value={data.eventsCount} icon={CalendarDays} />
              <StatCard label="Athlètes" value={data.athletesCount} icon={Users} />
              <StatCard label="Pays participants" value={data.countriesCount} icon={Globe} />
            </div>

            <div className="mt-6 grid grid-cols-1 gap-4 lg:grid-cols-2">
              <Card>
                <CardHeader>
                  <CardTitle>Épreuves par statut</CardTitle>
                  <CardDescription>Répartition des épreuves selon leur avancement</CardDescription>
                </CardHeader>
                <CardContent>
                  <EventStatusChart data={data.eventsByStatus} />
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>Athlètes par genre</CardTitle>
                  <CardDescription>Répartition de l'ensemble des athlètes inscrits</CardDescription>
                </CardHeader>
                <CardContent>
                  <GenderChart male={data.genderCounts.male} female={data.genderCounts.female} />
                </CardContent>
              </Card>
            </div>

            <Card className="mt-6">
              <CardHeader>
                <CardTitle>Athlètes par discipline</CardTitle>
                <CardDescription>Nombre d'athlètes inscrits dans chaque discipline</CardDescription>
              </CardHeader>
              <CardContent>
                {data.athletesByDiscipline.length === 0 ? (
                  <p className="py-8 text-center text-sm text-muted-foreground">
                    Aucune discipline enregistrée pour le moment.
                  </p>
                ) : (
                  <HorizontalRankingChart
                    data={data.athletesByDiscipline}
                    valueKey="count"
                    labelKey="name"
                    seriesName="Athlètes"
                    yAxisWidth={120}
                  />
                )}
              </CardContent>
            </Card>
          </>
        )}
      </QueryState>
    </div>
  )
}
