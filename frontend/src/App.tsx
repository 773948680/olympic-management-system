import { AppRoutes } from '@/routes/AppRoutes'
import { Toaster } from '@/components/ui/sonner'
import { AuthProvider } from '@/hooks/useAuth'

function App() {
  return (
    <AuthProvider>
      <AppRoutes />
      <Toaster position="top-right" richColors />
    </AuthProvider>
  )
}

export default App
