import { useEffect } from 'react'

/**
 * Steps the current page back when it lands past the last available page —
 * e.g. deleting the only row on the last page would otherwise strand the
 * user on a permanently-empty page with no obvious way back.
 */
export function useClampPage(totalPages: number | undefined, page: number, setPage: (page: number) => void) {
  useEffect(() => {
    if (totalPages !== undefined && totalPages > 0 && page >= totalPages) {
      setPage(totalPages - 1)
    }
  }, [totalPages, page, setPage])
}
