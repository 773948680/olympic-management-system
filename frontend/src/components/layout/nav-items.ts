import {
  LayoutDashboard,
  Users,
  Dumbbell,
  CalendarDays,
  ListOrdered,
  Medal,
  BarChart3,
  type LucideIcon,
} from 'lucide-react'

export interface NavItem {
  label: string
  href: string
  icon: LucideIcon
}

export const navItems: NavItem[] = [
  { label: 'Dashboard', href: '/dashboard', icon: LayoutDashboard },
  { label: 'Athlètes', href: '/athletes', icon: Users },
  { label: 'Disciplines', href: '/disciplines', icon: Dumbbell },
  { label: 'Épreuves', href: '/events', icon: CalendarDays },
  { label: 'Résultats', href: '/results', icon: ListOrdered },
  { label: 'Médailles', href: '/medals', icon: Medal },
  { label: 'Statistiques', href: '/statistics', icon: BarChart3 },
]
