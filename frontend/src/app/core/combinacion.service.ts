import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of, tap, throwError } from 'rxjs';
import { apiUrl } from './api.config';
import { CacheService } from './cache.service';
import { Prenda } from './prenda.service';

export interface Combinacion {
  id: string;
  profileId: number;
  prendas: Prenda[];
  esquemaColor: string;
}

@Injectable({ providedIn: 'root' })
export class CombinacionService {
  constructor(private http: HttpClient, private cache: CacheService) {}

  list(profileId: number): Observable<Combinacion[]> {
    const key = `combinaciones.${profileId}`;
    return this.http.get<Combinacion[]>(apiUrl('/api/combinaciones'), { params: { profileId: String(profileId) } }).pipe(
      tap((combos) => this.cache.set(key, combos)),
      catchError((err) => {
        const cached = this.cache.get<Combinacion[]>(key);
        return cached ? of(cached) : throwError(() => err);
      })
    );
  }
}
