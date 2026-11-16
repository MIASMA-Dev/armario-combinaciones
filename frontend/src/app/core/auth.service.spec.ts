import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  afterEach(() => {
    http.verify();
    localStorage.clear();
  });

  it('restore recupera el usuario si hay token guardado', () => {
    localStorage.setItem('armario.token', 'token-1');

    service.restore();

    const req = http.expectOne('/api/auth/me');
    expect(req.request.method).toBe('GET');
    req.flush({ id: 7, nombre: 'Ana', email: 'ana@armario.test' });

    expect(service.user()).toEqual({ id: 7, nombre: 'Ana', email: 'ana@armario.test' });
    expect(localStorage.getItem('armario.token')).toBe('token-1');
  });

  it('restore borra el token si el backend devuelve 401', () => {
    localStorage.setItem('armario.token', 'token-caducado');

    service.restore();

    const req = http.expectOne('/api/auth/me');
    req.flush('no autenticado', { status: 401, statusText: 'Unauthorized' });

    expect(service.user()).toBeNull();
    expect(localStorage.getItem('armario.token')).toBeNull();
  });

  it('restore no llama al backend sin token', () => {
    service.restore();
    http.expectNone('/api/auth/me');
    expect(service.user()).toBeNull();
  });
});
