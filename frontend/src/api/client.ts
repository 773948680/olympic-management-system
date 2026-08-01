import axios from 'axios'

export const AUTH_TOKEN_KEY = 'auth_token'
export const AUTH_USERNAME_KEY = 'auth_username'

export function clearAuthStorage() {
  localStorage.removeItem(AUTH_TOKEN_KEY)
  localStorage.removeItem(AUTH_USERNAME_KEY)
}

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
})

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem(AUTH_TOKEN_KEY)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

apiClient.interceptors.response.use(
  (response) => response,
  (error: unknown) => {
    if (import.meta.env.DEV && axios.isAxiosError(error)) {
      console.error('[API]', error.response?.status, error.config?.url, error.response?.data)
    }

    const isLoginRequest = axios.isAxiosError(error) && error.config?.url?.includes('/auth/login')
    if (axios.isAxiosError(error) && error.response?.status === 401 && !isLoginRequest) {
      clearAuthStorage()
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }

    return Promise.reject(error)
  },
)
