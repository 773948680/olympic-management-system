import { Trophy } from 'lucide-react'
import { Separator } from '@/components/ui/separator'
import { SidebarNav } from './SidebarNav'

export function Sidebar() {
  return (
    <aside className="hidden w-64 shrink-0 border-r border-border bg-sidebar md:flex md:flex-col">
      <div className="flex h-16 items-center gap-2 px-5">
        <Trophy className="size-6 text-primary" />
        <div className="leading-tight">
          <p className="text-sm font-semibold text-sidebar-foreground">Olympic Management</p>
          <p className="text-xs text-muted-foreground">JO de Dakar</p>
        </div>
      </div>
      <Separator />
      <div className="flex-1 overflow-y-auto py-4">
        <SidebarNav />
      </div>
    </aside>
  )
}
