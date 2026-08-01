import { useState } from 'react'
import { Navigate, useLocation, useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Trophy } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form'
import { Input } from '@/components/ui/input'
import { loginFormSchema, type LoginFormValues } from '@/schemas/auth'
import { useAuth } from '@/hooks/useAuth'
import { getErrorMessage } from '@/utils/api-error'

export default function LoginPage() {
  const { isAuthenticated, login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [error, setError] = useState<string | null>(null)
  const [isPending, setIsPending] = useState(false)

  const form = useForm<LoginFormValues>({
    resolver: zodResolver(loginFormSchema),
    defaultValues: { username: '', password: '' },
  })

  if (isAuthenticated) {
    const redirectTo = (location.state as { from?: string } | null)?.from ?? '/dashboard'
    return <Navigate to={redirectTo} replace />
  }

  async function onSubmit(values: LoginFormValues) {
    setError(null)
    setIsPending(true)
    try {
      await login(values.username, values.password)
      const redirectTo = (location.state as { from?: string } | null)?.from ?? '/dashboard'
      navigate(redirectTo, { replace: true })
    } catch (err) {
      setError(getErrorMessage(err))
    } finally {
      setIsPending(false)
    }
  }

  return (
    <div className="flex min-h-svh items-center justify-center bg-muted/30 p-4">
      <Card className="w-full max-w-sm">
        <CardHeader className="items-center text-center">
          <Trophy className="size-8 text-primary" />
          <CardTitle>Olympic Management</CardTitle>
          <CardDescription>JO de Dakar — Connexion</CardDescription>
        </CardHeader>
        <CardContent>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
              <FormField
                control={form.control}
                name="username"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Nom d'utilisateur</FormLabel>
                    <FormControl>
                      <Input placeholder="admin" autoComplete="username" autoFocus {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="password"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Mot de passe</FormLabel>
                    <FormControl>
                      <Input type="password" autoComplete="current-password" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              {error ? <p className="text-sm text-destructive">{error}</p> : null}
              <Button type="submit" className="w-full" disabled={isPending}>
                {isPending ? 'Connexion…' : 'Se connecter'}
              </Button>
            </form>
          </Form>
        </CardContent>
      </Card>
    </div>
  )
}
