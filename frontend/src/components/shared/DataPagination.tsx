import { ChevronLeft, ChevronRight } from 'lucide-react'
import { Button } from '@/components/ui/button'

interface DataPaginationProps {
  page: number
  totalPages: number
  totalElements: number
  onPageChange: (page: number) => void
}

export function DataPagination({ page, totalPages, totalElements, onPageChange }: DataPaginationProps) {
  if (totalElements === 0) return null

  return (
    <div className="flex items-center justify-between border-t px-4 py-3">
      <p className="text-sm text-muted-foreground">
        Page {page + 1} sur {Math.max(totalPages, 1)} — {totalElements} élément
        {totalElements > 1 ? 's' : ''}
      </p>
      <div className="flex items-center gap-2">
        <Button
          type="button"
          variant="outline"
          size="sm"
          disabled={page <= 0}
          onClick={() => onPageChange(page - 1)}
        >
          <ChevronLeft />
          Précédent
        </Button>
        <Button
          type="button"
          variant="outline"
          size="sm"
          disabled={page + 1 >= totalPages}
          onClick={() => onPageChange(page + 1)}
        >
          Suivant
          <ChevronRight />
        </Button>
      </div>
    </div>
  )
}
