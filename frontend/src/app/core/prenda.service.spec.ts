import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Prenda, PrendaService } from './prenda.service';
import { CacheService } from './cache.service';

describe('PrendaService', () => {
  let service: PrendaService;
  let http: HttpTestingController;
  let cache: CacheService;

  const prenda: Prenda = {
    id: 1,
    profileId: 1,
    nombre: 'Camisa',
    color: '#f2efe6',
    tipoPrenda: 'SUPERIOR',
    estado: 'DISPONIBLE'
  };

  const vaqueros: Prenda = {
    id: 2,
    profileId: 1,
    nombre: 'Vaqueros',
    color: '#3b5bdb',
    tipoPrenda: 'INFERIOR',
    estado: 'DISPONIBLE'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(PrendaService);
    http = TestBed.inject(HttpTestingController);
    cache = TestBed.inject(CacheService);
    localStorage.clear();
  });

  afterEach(() => {
    http.verify();
    localStorage.clear();
  });

  it('list guarda las prendas en cache', () => {
    let resultado: Prenda[] | undefined;
    service.list(1).subscribe((prendas) => (resultado = prendas));

    const req = http.expectOne('/api/prendas?profileId=1');
    req.flush([prenda]);

    expect(resultado).toEqual([prenda]);
    expect(cache.get('prendas.1')).toEqual([prenda]);
  });

  it('update reemplaza la prenda en cache', () => {
    cache.set('prendas.1', [prenda, vaqueros]);
    const actualizada = { ...prenda, estado: 'LAVANDERIA' as const };

    service.update(1, { estado: 'LAVANDERIA' }, 1).subscribe();

    const req = http.expectOne('/api/prendas/1');
    expect(req.request.method).toBe('PUT');
    req.flush(actualizada);

    expect(cache.get<Prenda[]>('prendas.1')).toEqual([actualizada, vaqueros]);
  });

  it('delete quita la prenda de cache', () => {
    cache.set('prendas.1', [prenda, vaqueros]);

    service.delete(1, 1).subscribe();

    const req = http.expectOne('/api/prendas/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);

    expect(cache.get<Prenda[]>('prendas.1')).toEqual([vaqueros]);
  });
});
