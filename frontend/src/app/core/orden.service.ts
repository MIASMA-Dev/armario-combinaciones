import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of, tap, throwError } from 'rxjs';
import { apiUrl } from './api.config';
import { CacheService } from './cache.service';

export interface Orden {
  id: string;
  profileId: number;
  prendaId?: number | null;
  nombre: string;
  categoria: string;
  tipoAccion: 'REVISAR' | 'REPONER';
  prioridad: 'ALTA' | 'MEDIA' | 'BAJA';
  motivo: string;
  fechaReferencia: string;
  diasSinUso: number;
}

@Injectable({ providedIn: 'root' })
export class OrdenService {
  constructor(private http: HttpClient, private cache: CacheService) {}

  list(profileId: number): Observable<Orden[]> {
    const key = `ordenes.${profileId}`;
    return this.http.get<Orden[]>(apiUrl('/api/ordenes/generar'), { params: { profileId: String(profileId) } }).pipe(
      tap((ordenes) => this.cache.set(key, ordenes)),
      catchError((err) => {
        const cached = this.cache.get<Orden[]>(key);
        return cached ? of(cached) : throwError(() => err);
      })
    );
  }
}
