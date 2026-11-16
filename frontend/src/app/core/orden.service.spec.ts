import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { OrdenService } from './orden.service';
import { CacheService } from './cache.service';

describe('OrdenService', () => {
  let service: OrdenService;
  let http: HttpTestingController;
  let cache: CacheService;

  const orden = {
    id: 'inactividad-1',
    profileId: 1,
    prendaId: 1,
    nombre: 'Trenca',
    categoria: 'ABRIGOS',
    tipoAccion: 'REVISAR' as const,
    prioridad: 'BAJA' as const,
    motivo: 'Sin uso desde hace 200 días',
    fechaReferencia: '2026-01-01T00:00:00Z',
    diasSinUso: 200
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(OrdenService);
    http = TestBed.inject(HttpTestingController);
    cache = TestBed.inject(CacheService);
    localStorage.clear();
  });

  afterEach(() => {
    http.verify();
    localStorage.clear();
  });

  it('carga ordenes y las guarda en cache', () => {
    let resultado: unknown;
    service.list(1).subscribe((ordenes) => (resultado = ordenes));

    const req = http.expectOne('/api/ordenes/generar?profileId=1');
    expect(req.request.method).toBe('GET');
    req.flush([orden]);

    expect(resultado).toEqual([orden]);
    expect(cache.get('ordenes.1')).toEqual([orden]);
  });

  it('usa el cache cuando falla la red', () => {
    cache.set('ordenes.1', [orden]);

    let resultado: unknown;
    service.list(1).subscribe((ordenes) => (resultado = ordenes));

    const req = http.expectOne('/api/ordenes/generar?profileId=1');
    req.flush('sin conexion', { status: 503, statusText: 'Service Unavailable' });

    expect(resultado).toEqual([orden]);
  });
});
