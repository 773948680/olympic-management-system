import { lazy, Suspense } from 'react'
import { Navigate, Route, Routes } from 'react-router-dom'
import { DashboardLayout } from '@/layouts/DashboardLayout'
import { ProtectedRoute } from '@/components/auth/ProtectedRoute'
import { Skeleton } from '@/components/ui/skeleton'

const LoginPage = lazy(() => import('@/pages/LoginPage'))
const DashboardPage = lazy(() => import('@/pages/DashboardPage'))
const AthletesPage = lazy(() => import('@/pages/AthletesPage'))
const DisciplinesPage = lazy(() => import('@/pages/DisciplinesPage'))
const EventsPage = lazy(() => import('@/pages/EventsPage'))
const EventPodiumPage = lazy(() => import('@/pages/EventPodiumPage'))
const ResultsPage = lazy(() => import('@/pages/ResultsPage'))
const MedalsPage = lazy(() => import('@/pages/MedalsPage'))
const StatisticsPage = lazy(() => import('@/pages/StatisticsPage'))
const NotFoundPage = lazy(() => import('@/pages/NotFoundPage'))

function PageFallback() {
  return (
    <div className="space-y-4">
      <Skeleton className="h-8 w-48" />
      <Skeleton className="h-64 w-full" />
    </div>
  )
}

export function AppRoutes() {
  return (
    <Suspense fallback={<PageFallback />}>
      <Routes>
        <Route path="login" element={<LoginPage />} />
        <Route element={<ProtectedRoute />}>
          <Route element={<DashboardLayout />}>
            <Route index element={<Navigate to="/dashboard" replace />} />
            <Route path="dashboard" element={<DashboardPage />} />
            <Route path="athletes" element={<AthletesPage />} />
            <Route path="disciplines" element={<DisciplinesPage />} />
            <Route path="events" element={<EventsPage />} />
            <Route path="events/:id/podium" element={<EventPodiumPage />} />
            <Route path="results" element={<ResultsPage />} />
            <Route path="medals" element={<MedalsPage />} />
            <Route path="statistics" element={<StatisticsPage />} />
          </Route>
        </Route>
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </Suspense>
  )
}
