import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of, tap, throwError } from 'rxjs';
import { apiUrl } from './api.config';
import { CacheService } from './cache.service';

export interface Profile {
  id: number;
  nombre: string;
  tipo: 'HOMBRE' | 'MUJER' | 'NINO' | 'BEBE';
  ownerId: number;
  sharedWith: number[];
}

@Injectable({ providedIn: 'root' })
export class ProfileService {
  constructor(private http: HttpClient, private cache: CacheService) {}

  list(): Observable<Profile[]> {
    return this.http.get<Profile[]>(apiUrl('/api/profiles')).pipe(
      tap((profiles) => this.cache.set('profiles', profiles)),
      catchError((err) => {
        const cached = this.cache.get<Profile[]>('profiles');
        return cached ? of(cached) : throwError(() => err);
      })
    );
  }

  create(nombre: string, tipo: string): Observable<Profile> {
    return this.http.post<Profile>(apiUrl('/api/profiles'), { nombre, tipo }).pipe(
      tap((profile) => this.actualizarCache((profiles) => [...profiles, profile]))
    );
  }

  share(id: number, email: string): Observable<Profile> {
    return this.http.post<Profile>(apiUrl(`/api/profiles/${id}/share`), { email }).pipe(
      tap((profile) =>
        this.actualizarCache((profiles) => profiles.map((p) => (p.id === profile.id ? profile : p)))
      )
    );
  }

  private actualizarCache(fn: (profiles: Profile[]) => Profile[]): void {
    const actuales = this.cache.get<Profile[]>('profiles');
    if (actuales) this.cache.set('profiles', fn(actuales));
  }
}
