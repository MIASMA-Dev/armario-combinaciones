import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class CacheService {
  private readonly prefijo = 'armario.cache.';

  get<T>(key: string): T | null {
    try {
      const raw = localStorage.getItem(this.prefijo + key);
      return raw ? (JSON.parse(raw) as T) : null;
    } catch {
      return null;
    }
  }

  set<T>(key: string, value: T): void {
    try {
      localStorage.setItem(this.prefijo + key, JSON.stringify(value));
    } catch {
      // Sin espacio o almacenamiento bloqueado: se ignora el cache.
    }
  }

  clear(): void {
    try {
      for (const key of Object.keys(localStorage)) {
        if (key.startsWith(this.prefijo)) localStorage.removeItem(key);
      }
    } catch {
      // Sin acceso a localStorage.
    }
  }
}
