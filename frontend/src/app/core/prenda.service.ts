import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of, tap, throwError } from 'rxjs';
import { apiUrl } from './api.config';
import { CacheService } from './cache.service';

export interface Prenda {
  id: number;
  profileId: number;
  nombre: string;
  color?: string;
  temporada?: string;
  talla?: string;
  tipoPrenda: string;
  estado: 'DISPONIBLE' | 'EN_USO' | 'LAVANDERIA' | 'PENDIENTE';
  imagenUrl?: string;
  tejido?: string;
}

export interface PrendaRequest {
  profileId: number;
  nombre: string;
  color?: string;
  temporada?: string;
  talla?: string;
  tipoPrenda: string;
  estado?: string;
  imagenUrl?: string;
  tejido?: string;
}

export type PrendaUpdate = Partial<Omit<PrendaRequest, 'profileId'>>;

@Injectable({ providedIn: 'root' })
export class PrendaService {
  constructor(private http: HttpClient, private cache: CacheService) {}

  list(profileId: number): Observable<Prenda[]> {
    const key = `prendas.${profileId}`;
    return this.http.get<Prenda[]>(apiUrl('/api/prendas'), { params: { profileId: String(profileId) } }).pipe(
      tap((prendas) => this.cache.set(key, prendas)),
      catchError((err) => {
        const cached = this.cache.get<Prenda[]>(key);
        return cached ? of(cached) : throwError(() => err);
      })
    );
  }

  create(req: PrendaRequest): Observable<Prenda> {
    return this.http.post<Prenda>(apiUrl('/api/prendas'), req).pipe(
      tap((prenda) => this.actualizarCache(req.profileId, (prendas) => [...prendas, prenda]))
    );
  }

  update(id: number, cambios: PrendaUpdate, profileId: number): Observable<Prenda> {
    return this.http.put<Prenda>(apiUrl(`/api/prendas/${id}`), cambios).pipe(
      tap((prenda) =>
        this.actualizarCache(profileId, (prendas) => prendas.map((p) => (p.id === id ? prenda : p)))
      )
    );
  }

  delete(id: number, profileId: number): Observable<void> {
    return this.http.delete<void>(apiUrl(`/api/prendas/${id}`)).pipe(
      tap(() => this.actualizarCache(profileId, (prendas) => prendas.filter((p) => p.id !== id)))
    );
  }

  private actualizarCache(profileId: number, fn: (prendas: Prenda[]) => Prenda[]): void {
    const key = `prendas.${profileId}`;
    const actuales = this.cache.get<Prenda[]>(key);
    if (actuales) this.cache.set(key, fn(actuales));
  }
}
