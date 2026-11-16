import { TestBed } from '@angular/core/testing';
import { CacheService } from './cache.service';

describe('CacheService', () => {
  let service: CacheService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CacheService);
    localStorage.clear();
  });

  afterEach(() => localStorage.clear());

  it('guarda y recupera valores', () => {
    service.set('prendas.1', [{ id: 1, nombre: 'Camisa' }]);
    expect(service.get<{ id: number; nombre: string }[]>('prendas.1')).toEqual([
      { id: 1, nombre: 'Camisa' }
    ]);
  });

  it('devuelve null si no hay cache', () => {
    expect(service.get('inexistente')).toBeNull();
  });

  it('clear elimina solo las claves de cache', () => {
    localStorage.setItem('otra.clave', 'valor');
    service.set('combinaciones.1', []);

    service.clear();

    expect(service.get('combinaciones.1')).toBeNull();
    expect(localStorage.getItem('otra.clave')).toBe('valor');
  });
});
