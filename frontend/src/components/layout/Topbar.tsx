import { useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { LogOut, Menu, Trophy, User } from 'lucide-react'
import { Button } from '@/components/ui/button'
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from '@/components/ui/sheet'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { useAuth } from '@/hooks/useAuth'
import { navItems } from './nav-items'
import { SidebarNav } from './SidebarNav'

export function Topbar() {
  const [mobileNavOpen, setMobileNavOpen] = useState(false)
  const location = useLocation()
  const navigate = useNavigate()
  const { username, logout } = useAuth()

  function handleLogout() {
    logout()
    navigate('/login', { replace: true })
  }

  const currentTitle =
    navItems.find((item) => location.pathname.startsWith(item.href))?.label ?? 'Olympic Management'

  return (
    <header className="sticky top-0 z-40 flex h-16 items-center gap-3 border-b border-border bg-background px-4 md:px-6">
      <Sheet open={mobileNavOpen} onOpenChange={setMobileNavOpen}>
        <SheetTrigger asChild>
          <Button variant="ghost" size="icon" className="md:hidden">
            <Menu className="size-5" />
            <span className="sr-only">Ouvrir le menu</span>
          </Button>
        </SheetTrigger>
        <SheetContent side="left" className="w-72 p-0">
          <SheetHeader className="flex-row items-center gap-2 border-b border-border">
            <Trophy className="size-5 text-primary" />
            <SheetTitle>Olympic Management</SheetTitle>
          </SheetHeader>
          <div className="py-4">
            <SidebarNav onNavigate={() => setMobileNavOpen(false)} />
          </div>
        </SheetContent>
      </Sheet>

      <h1 className="text-base font-semibold">{currentTitle}</h1>

      <div className="ml-auto flex items-center gap-2">
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" size="icon" className="rounded-full">
              <Avatar className="size-8">
                <AvatarFallback>
                  <User className="size-4" />
                </AvatarFallback>
              </Avatar>
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <DropdownMenuLabel>{username ?? 'Comité Olympique'}</DropdownMenuLabel>
            <DropdownMenuSeparator />
            <DropdownMenuItem disabled>Profil</DropdownMenuItem>
            <DropdownMenuItem disabled>Paramètres</DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem onClick={handleLogout} variant="destructive">
              <LogOut />
              Déconnexion
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </header>
  )
}
