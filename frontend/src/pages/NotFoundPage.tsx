import { Link } from 'react-router-dom'
import { Button } from '@/components/ui/button'

export default function NotFoundPage() {
  return (
    <div className="flex min-h-svh flex-col items-center justify-center gap-4 text-center">
      <p className="text-6xl font-bold text-primary">404</p>
      <p className="text-muted-foreground">Cette page n'existe pas.</p>
      <Button asChild>
        <Link to="/dashboard">Retour au dashboard</Link>
      </Button>
    </div>
  )
}
