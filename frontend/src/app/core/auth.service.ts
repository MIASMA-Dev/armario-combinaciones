import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { Observable, finalize, tap } from 'rxjs';
import { apiUrl } from './api.config';

export interface AuthUser {
  id: number;
  nombre: string;
  email: string;
  token?: string;
}

const TOKEN_KEY = 'armario.token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  readonly user = signal<AuthUser | null>(null);

  constructor(private http: HttpClient) {}

  register(nombre: string, email: string, password: string): Observable<AuthUser> {
    return this.http.post<AuthUser>(apiUrl('/api/auth/register'), { nombre, email, password })
      .pipe(tap((u) => this.guardar(u)));
  }

  login(email: string, password: string): Observable<AuthUser> {
    return this.http.post<AuthUser>(apiUrl('/api/auth/login'), { email, password })
      .pipe(tap((u) => this.guardar(u)));
  }

  logout(): void {
    this.http.post(apiUrl('/api/auth/logout'), {})
      .pipe(finalize(() => this.limpiar()))
      .subscribe({ error: () => {} });
  }

  /** Restaura la sesion guardada al recargar la pagina. */
  restore(): void {
    let token: string | null = null;
    try {
      token = localStorage.getItem(TOKEN_KEY);
    } catch {
      token = null;
    }
    if (!token) return;

    this.http.get<AuthUser>(apiUrl('/api/auth/me')).subscribe({
      next: (u) => this.user.set({ id: u.id, nombre: u.nombre, email: u.email }),
      error: (err) => {
        if (err?.status === 401) this.limpiar();
      }
    });
  }

  private guardar(user: AuthUser): void {
    if (user.token) {
      try {
        localStorage.setItem(TOKEN_KEY, user.token);
      } catch {
        // Sin acceso a localStorage: la sesion por cookie sigue funcionando en web.
      }
    }
    this.user.set({ id: user.id, nombre: user.nombre, email: user.email });
  }

  private limpiar(): void {
    try {
      localStorage.removeItem(TOKEN_KEY);
    } catch {
      // Ignorado.
    }
    this.user.set(null);
  }
}
