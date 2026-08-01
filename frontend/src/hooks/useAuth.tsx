import { createContext, useContext, useState, type ReactNode } from 'react'
import { authApi, AUTH_TOKEN_KEY, AUTH_USERNAME_KEY, clearAuthStorage } from '@/api'

interface AuthContextValue {
  username: string | null
  isAuthenticated: boolean
  login: (username: string, password: string) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [username, setUsername] = useState<string | null>(() => localStorage.getItem(AUTH_USERNAME_KEY))

  async function login(usernameInput: string, password: string) {
    const response = await authApi.login({ username: usernameInput, password })
    localStorage.setItem(AUTH_TOKEN_KEY, response.token)
    localStorage.setItem(AUTH_USERNAME_KEY, response.username)
    setUsername(response.username)
  }

  function logout() {
    clearAuthStorage()
    setUsername(null)
  }

  return (
    <AuthContext.Provider value={{ username, isAuthenticated: !!username, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth doit être utilisé à l’intérieur de <AuthProvider>')
  }
  return context
}
