import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { CombinacionService } from './combinacion.service';
import { CacheService } from './cache.service';

describe('CombinacionService', () => {
  let service: CombinacionService;
  let http: HttpTestingController;
  let cache: CacheService;

  const combo = {
    id: 'c1',
    profileId: 1,
    esquemaColor: 'neutro',
    prendas: [
      {
        id: 1,
        profileId: 1,
        nombre: 'Camisa',
        tipoPrenda: 'SUPERIOR',
        estado: 'DISPONIBLE' as const
      }
    ]
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(CombinacionService);
    http = TestBed.inject(HttpTestingController);
    cache = TestBed.inject(CacheService);
    localStorage.clear();
  });

  afterEach(() => {
    http.verify();
    localStorage.clear();
  });

  it('carga combinaciones y las guarda en cache', () => {
    let resultado: unknown;
    service.list(1).subscribe((combos) => (resultado = combos));

    const req = http.expectOne('/api/combinaciones?profileId=1');
    expect(req.request.method).toBe('GET');
    req.flush([combo]);

    expect(resultado).toEqual([combo]);
    expect(cache.get('combinaciones.1')).toEqual([combo]);
  });

  it('usa el cache cuando falla la red', () => {
    cache.set('combinaciones.1', [combo]);

    let resultado: unknown;
    service.list(1).subscribe((combos) => (resultado = combos));

    const req = http.expectOne('/api/combinaciones?profileId=1');
    req.flush('sin conexion', { status: 500, statusText: 'Server Error' });

    expect(resultado).toEqual([combo]);
  });
});
