import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Profile, ProfileService } from './profile.service';
import { CacheService } from './cache.service';

describe('ProfileService', () => {
  let service: ProfileService;
  let http: HttpTestingController;
  let cache: CacheService;

  const perfil: Profile = { id: 1, nombre: 'Ana', tipo: 'MUJER', ownerId: 1, sharedWith: [] };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(ProfileService);
    http = TestBed.inject(HttpTestingController);
    cache = TestBed.inject(CacheService);
    localStorage.clear();
  });

  afterEach(() => {
    http.verify();
    localStorage.clear();
  });

  it('create añade el perfil al cache', () => {
    cache.set('profiles', [perfil]);
    const nuevo: Profile = { id: 2, nombre: 'Peque', tipo: 'NINO', ownerId: 1, sharedWith: [] };

    service.create('Peque', 'NINO').subscribe();

    const req = http.expectOne('/api/profiles');
    expect(req.request.method).toBe('POST');
    req.flush(nuevo);

    expect(cache.get<Profile[]>('profiles')).toEqual([perfil, nuevo]);
  });

  it('share actualiza el perfil en cache', () => {
    cache.set('profiles', [perfil]);
    const compartido: Profile = { ...perfil, sharedWith: [2] };

    service.share(1, 'carlos@armario.test').subscribe();

    const req = http.expectOne('/api/profiles/1/share');
    expect(req.request.body).toEqual({ email: 'carlos@armario.test' });
    req.flush(compartido);

    expect(cache.get<Profile[]>('profiles')).toEqual([compartido]);
  });
});
