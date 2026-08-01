import { Card, CardContent } from '@/components/ui/card'
import { PageHeader } from './PageHeader'

interface PlaceholderPageProps {
  title: string
  description?: string
}

export function PlaceholderPage({ title, description }: PlaceholderPageProps) {
  return (
    <div>
      <PageHeader title={title} description={description} />
      <Card>
        <CardContent className="py-16 text-center text-sm text-muted-foreground">
          Cette page sera implémentée prochainement.
        </CardContent>
      </Card>
    </div>
  )
}
