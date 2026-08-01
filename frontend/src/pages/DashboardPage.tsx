import { Award, Globe, Medal, Users } from 'lucide-react'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { PageHeader } from '@/components/shared/PageHeader'
import { StatCard } from '@/components/shared/StatCard'
import { QueryState } from '@/components/shared/QueryState'
import { MedalsByNationChart } from '@/components/dashboard/MedalsByNationChart'
import { MedalDistributionChart } from '@/components/dashboard/MedalDistributionChart'
import { NationSummaryTable } from '@/components/dashboard/NationSummaryTable'
import { HorizontalRankingChart } from '@/components/shared/HorizontalRankingChart'
import {
  useAthletesCount,
  useCountriesCount,
  useCountriesMedalists,
  useCountriesRanking,
  useMedalTotals,
} from '@/services/dashboard'
import { useMedalTable } from '@/services/medals'
import { buildNationSummary } from '@/utils/nation-summary'

const chartSkeleton = <Skeleton className="h-[260px] w-full" />
const tableSkeleton = (
  <div className="space-y-2 p-4">
    {Array.from({ length: 5 }).map((_, i) => (
      <Skeleton key={i} className="h-8 w-full" />
    ))}
  </div>
)

export default function DashboardPage() {
  const athletesCount = useAthletesCount()
  const countriesCount = useCountriesCount()
  const medalTotals = useMedalTotals()
  const countriesRanking = useCountriesRanking()
  const countriesMedalists = useCountriesMedalists()
  const medalTable = useMedalTable()

  const summaryPending =
    medalTable.isPending || countriesRanking.isPending || countriesMedalists.isPending
  const summaryError = medalTable.isError || countriesRanking.isError || countriesMedalists.isError
  const summaryData = buildNationSummary(
    medalTable.data ?? [],
    countriesRanking.data ?? [],
    countriesMedalists.data ?? [],
  )

  return (
    <div>
      <PageHeader
        title="Dashboard"
        description="Vue d'ensemble des Jeux Olympiques de Dakar"
      />

      {/* Cards */}
      <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-6">
        <StatCard
          label="Athlètes"
          value={athletesCount.data?.count ?? 0}
          icon={Users}
          loading={athletesCount.isPending}
          error={athletesCount.isError}
          onRetry={() => athletesCount.refetch()}
        />
        <StatCard
          label="Pays participants"
          value={countriesCount.data?.count ?? 0}
          icon={Globe}
          loading={countriesCount.isPending}
          error={countriesCount.isError}
          onRetry={() => countriesCount.refetch()}
        />
        <StatCard
          label="Médailles d'or"
          value={medalTotals.data?.gold ?? 0}
          icon={Medal}
          iconClassName="text-[#eda100] bg-[#eda100]/10"
          loading={medalTotals.isPending}
          error={medalTotals.isError}
          onRetry={() => medalTotals.refetch()}
        />
        <StatCard
          label="Médailles d'argent"
          value={medalTotals.data?.silver ?? 0}
          icon={Medal}
          iconClassName="text-[#2a78d6] bg-[#2a78d6]/10"
          loading={medalTotals.isPending}
          error={medalTotals.isError}
          onRetry={() => medalTotals.refetch()}
        />
        <StatCard
          label="Médailles de bronze"
          value={medalTotals.data?.bronze ?? 0}
          icon={Medal}
          iconClassName="text-[#eb6834] bg-[#eb6834]/10"
          loading={medalTotals.isPending}
          error={medalTotals.isError}
          onRetry={() => medalTotals.refetch()}
        />
        <StatCard
          label="Total médailles"
          value={medalTotals.data?.total ?? 0}
          icon={Award}
          loading={medalTotals.isPending}
          error={medalTotals.isError}
          onRetry={() => medalTotals.refetch()}
        />
      </div>

      {/* Charts */}
      <div className="mt-6 grid grid-cols-1 gap-4 lg:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>Médailles par nation</CardTitle>
            <CardDescription>Top 8 — répartition Or / Argent / Bronze</CardDescription>
          </CardHeader>
          <CardContent>
            <QueryState
              isPending={medalTable.isPending}
              isError={medalTable.isError}
              error={medalTable.error}
              data={medalTable.data}
              isEmpty={(d) => d.length === 0}
              emptyMessage="Aucune médaille enregistrée pour le moment."
              onRetry={() => medalTable.refetch()}
              loading={chartSkeleton}
            >
              {(data) => <MedalsByNationChart data={data} />}
            </QueryState>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Classement par points</CardTitle>
            <CardDescription>Or = 7 pts, Argent = 4 pts, Bronze = 1 pt</CardDescription>
          </CardHeader>
          <CardContent>
            <QueryState
              isPending={countriesRanking.isPending}
              isError={countriesRanking.isError}
              error={countriesRanking.error}
              data={countriesRanking.data}
              isEmpty={(d) => d.length === 0}
              emptyMessage="Aucun pays classé pour le moment."
              onRetry={() => countriesRanking.refetch()}
              loading={chartSkeleton}
            >
              {(data) => (
                <HorizontalRankingChart
                  data={data}
                  valueKey="points"
                  labelKey="nationality"
                  seriesName="Points"
                  topN={8}
                />
              )}
            </QueryState>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Répartition Or / Argent / Bronze</CardTitle>
            <CardDescription>Part de chaque type de médaille, toutes nations confondues</CardDescription>
          </CardHeader>
          <CardContent>
            <QueryState
              isPending={medalTotals.isPending}
              isError={medalTotals.isError}
              error={medalTotals.error}
              data={medalTotals.data}
              isEmpty={(d) => d.total === 0}
              emptyMessage="Aucune médaille attribuée pour le moment."
              onRetry={() => medalTotals.refetch()}
              loading={chartSkeleton}
            >
              {(data) => <MedalDistributionChart data={data} />}
            </QueryState>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Athlètes médaillés par pays</CardTitle>
            <CardDescription>Top 8 — athlètes distincts ayant remporté au moins une médaille</CardDescription>
          </CardHeader>
          <CardContent>
            <QueryState
              isPending={countriesMedalists.isPending}
              isError={countriesMedalists.isError}
              error={countriesMedalists.error}
              data={countriesMedalists.data}
              isEmpty={(d) => d.length === 0}
              emptyMessage="Aucun athlète médaillé pour le moment."
              onRetry={() => countriesMedalists.refetch()}
              loading={chartSkeleton}
            >
              {(data) => (
                <HorizontalRankingChart
                  data={data}
                  valueKey="medalists"
                  labelKey="nationality"
                  seriesName="Athlètes médaillés"
                  topN={8}
                />
              )}
            </QueryState>
          </CardContent>
        </Card>
      </div>

      {/* Table */}
      <Card className="mt-6">
        <CardHeader>
          <CardTitle>Tableau détaillé par nation</CardTitle>
          <CardDescription>
            Vue table de l'ensemble des graphiques ci-dessus, trié Or puis Argent puis Bronze
          </CardDescription>
        </CardHeader>
        <CardContent className="px-0">
          <QueryState
            isPending={summaryPending}
            isError={summaryError}
            data={summaryData}
            isEmpty={(d) => d.length === 0}
            emptyMessage="Aucune donnée à afficher pour le moment."
            onRetry={() => {
              medalTable.refetch()
              countriesRanking.refetch()
              countriesMedalists.refetch()
            }}
            loading={tableSkeleton}
          >
            {(data) => <NationSummaryTable data={data} />}
          </QueryState>
        </CardContent>
      </Card>
    </div>
  )
}
