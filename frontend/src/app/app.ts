import { Component, computed, effect, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from './core/auth.service';
import { Login } from './features/login/login';
import { Profile, ProfileService } from './core/profile.service';
import { Prenda, PrendaService } from './core/prenda.service';
import { VisionResult, VisionService } from './core/vision.service';
import { Combinacion, CombinacionService } from './core/combinacion.service';
import { Orden, OrdenService } from './core/orden.service';
import { CacheService } from './core/cache.service';
import { ConnectivityService } from './core/connectivity.service';

@Component({
  selector: 'app-root',
  imports: [Login, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('Armario y Combinaciones');
  protected readonly profiles = signal<Profile[]>([]);
  protected readonly selectedProfileId = signal<number | null>(null);
  protected readonly prendas = signal<Prenda[]>([]);
  protected readonly combinaciones = signal<Combinacion[]>([]);
  protected readonly ordenes = signal<Orden[]>([]);
  protected readonly activeTab = signal<'inventario' | 'combinaciones' | 'ordenes'>('inventario');
  protected readonly filtroColor = signal('');
  protected readonly filtroTipo = signal('');
  protected readonly filtroColorInv = signal('');
  protected readonly filtroTemporada = signal('');
  protected readonly filtroEstado = signal('');
  protected readonly error = signal('');

  protected readonly showCapture = signal(false);
  protected readonly showProfiles = signal(false);
  protected readonly profileMsg = signal('');
  protected newProfileName = '';
  protected newProfileTipo = 'MUJER';
  protected shareEmail = '';
  protected readonly vision = signal<VisionResult | null>(null);
  protected readonly analyzing = signal(false);
  protected captureName = '';
  protected captureColor = '';
  protected captureTipo = 'SUPERIOR';
  protected captureTemporada = 'Todo el año';
  protected captureTalla = '';

  protected readonly estadoLabel: Record<string, string> = {
    DISPONIBLE: 'Disponible',
    EN_USO: 'En uso',
    LAVANDERIA: 'Lavandería',
    PENDIENTE: 'Pendiente'
  };

  protected readonly chipColor: Record<string, string> = {
    DISPONIBLE: '#2fbf8a',
    EN_USO: '#7d5cff',
    LAVANDERIA: '#2b9fd9',
    PENDIENTE: '#ff8a3d'
  };

  protected readonly tipoLabel: Record<string, string> = {
    SUPERIOR: 'Superior',
    INFERIOR: 'Inferior',
    ROPA_INTERIOR: 'Ropa interior',
    CALCETINES: 'Calcetines',
    ZAPATOS: 'Zapatos',
    COMPLEMENTOS: 'Complementos',
    JOYAS: 'Joyas',
    SOMBREROS: 'Sombreros',
    CHAQUETAS: 'Chaquetas',
    ABRIGOS: 'Abrigos'
  };

  protected readonly tiposDisponibles = Object.entries(this.tipoLabel).map(([key, label]) => ({ key, label }));

  protected readonly prioridadLabel: Record<string, string> = { ALTA: 'Alta', MEDIA: 'Media', BAJA: 'Baja' };

  protected readonly prioridadColor: Record<string, string> = {
    ALTA: '#c8102e',
    MEDIA: '#ff8a3d',
    BAJA: '#2b9fd9'
  };

  protected readonly estadosDisponibles = Object.entries(this.estadoLabel).map(([key, label]) => ({ key, label }));

  protected readonly temporadasDisponibles = computed(() => {
    const set = new Set<string>();
    for (const p of this.prendas()) {
      if (p.temporada) set.add(p.temporada);
    }
    return [...set].sort();
  });

  protected readonly coloresInventario = computed(() => {
    const set = new Set<string>();
    for (const p of this.prendas()) {
      if (p.color) set.add(p.color);
    }
    return [...set].sort();
  });

  protected readonly selectedProfile = computed(
    () => this.profiles().find((p) => p.id === this.selectedProfileId()) ?? null
  );

  protected readonly puedeCompartir = computed(
    () => this.selectedProfile()?.ownerId === this.auth.user()?.id
  );

  protected readonly prendasFiltradas = computed(() => {
    const color = this.filtroColorInv();
    const temporada = this.filtroTemporada();
    const estado = this.filtroEstado();
    return this.prendas().filter(
      (p) =>
        (!color || p.color === color) &&
        (!temporada || p.temporada === temporada) &&
        (!estado || p.estado === estado)
    );
  });

  protected readonly coloresDisponibles = computed(() => {
    const set = new Set<string>();
    for (const c of this.combinaciones()) {
      for (const p of c.prendas) {
        if (p.color) set.add(p.color);
      }
    }
    return [...set].sort();
  });

  protected readonly combinacionesFiltradas = computed(() => {
    const color = this.filtroColor();
    const tipo = this.filtroTipo();
    return this.combinaciones().filter((c) =>
      (!color || c.prendas.some((p) => p.color === color)) &&
      (!tipo || c.prendas.some((p) => p.tipoPrenda === tipo))
    );
  });

  constructor(
    protected readonly auth: AuthService,
    private readonly profileService: ProfileService,
    private readonly prendaService: PrendaService,
    private readonly visionService: VisionService,
    private readonly combinacionService: CombinacionService,
    private readonly ordenService: OrdenService,
    private readonly cache: CacheService,
    protected readonly connectivity: ConnectivityService
  ) {
    this.auth.restore();
    effect(() => {
      const user = this.auth.user();
      if (user) {
        this.loadProfiles();
      } else {
        this.profiles.set([]);
        this.prendas.set([]);
        this.combinaciones.set([]);
        this.ordenes.set([]);
        this.selectedProfileId.set(null);
      }
    });
  }

  private loadProfiles(): void {
    this.profileService.list().subscribe({
      next: (profiles) => {
        this.profiles.set(profiles);
        if (profiles.length > 0 && this.selectedProfileId() === null) {
          this.selectProfile(profiles[0].id);
        }
      },
      error: () => this.error.set('No se pudieron cargar los perfiles')
    });
  }

  protected selectProfile(id: number): void {
    this.selectedProfileId.set(id);
    this.prendaService.list(id).subscribe({
      next: (prendas) => this.prendas.set(prendas),
      error: () => this.error.set('No se pudieron cargar las prendas')
    });
    if (this.activeTab() === 'combinaciones') {
      this.showCombinaciones();
    } else if (this.activeTab() === 'ordenes') {
      this.showOrdenes();
    }
  }

  protected showCombinaciones(): void {
    this.activeTab.set('combinaciones');
    const profileId = this.selectedProfileId();
    if (!profileId) return;
    this.combinacionService.list(profileId).subscribe({
      next: (combos) => this.combinaciones.set(combos),
      error: () => this.error.set('No se pudieron generar las combinaciones')
    });
  }

  protected showInventario(): void {
    this.activeTab.set('inventario');
  }

  protected showOrdenes(): void {
    this.activeTab.set('ordenes');
    const profileId = this.selectedProfileId();
    if (!profileId) return;
    this.ordenService.list(profileId).subscribe({
      next: (ordenes) => this.ordenes.set(ordenes),
      error: () => this.error.set('No se pudieron cargar las órdenes')
    });
  }

  protected marcarEnUso(orden: Orden): void {
    if (!orden.prendaId) return;
    const prenda = this.prendas().find((p) => p.id === orden.prendaId);
    if (prenda) this.cambiarEstado(prenda, 'EN_USO');
  }

  protected capturarDe(orden: Orden): void {
    this.openCapture();
    this.captureTipo = orden.categoria;
  }

  protected logout(): void {
    this.cache.clear();
    this.auth.logout();
  }

  protected cambiarEstado(prenda: Prenda, estado: string): void {
    const profileId = this.selectedProfileId();
    if (!profileId) return;
    this.prendaService.update(prenda.id, { estado }, profileId).subscribe({
      next: (actualizada) => {
        this.prendas.update((list) => list.map((p) => (p.id === prenda.id ? actualizada : p)));
        if (this.activeTab() === 'combinaciones') this.showCombinaciones();
        else if (this.activeTab() === 'ordenes') this.showOrdenes();
      },
      error: () => this.error.set('No se pudo cambiar el estado')
    });
  }

  protected eliminarPrenda(prenda: Prenda): void {
    const profileId = this.selectedProfileId();
    if (!profileId) return;
    if (!confirm(`¿Eliminar "${prenda.nombre}" del armario?`)) return;
    this.prendaService.delete(prenda.id, profileId).subscribe({
      next: () => this.prendas.update((list) => list.filter((p) => p.id !== prenda.id)),
      error: () => this.error.set('No se pudo eliminar la prenda')
    });
  }

  protected estado(prenda: Prenda): string {
    return this.estadoLabel[prenda.estado] ?? prenda.estado;
  }

  protected color(prenda: Prenda): string {
    return this.chipColor[prenda.estado] ?? '#7d6a8c';
  }

  protected openProfiles(): void {
    this.showProfiles.set(true);
    this.profileMsg.set('');
    this.error.set('');
    this.newProfileName = '';
    this.shareEmail = '';
  }

  protected closeProfiles(): void {
    this.showProfiles.set(false);
  }

  protected crearPerfil(): void {
    const nombre = this.newProfileName.trim();
    if (!nombre) {
      this.error.set('El perfil necesita un nombre');
      return;
    }
    this.profileService.create(nombre, this.newProfileTipo).subscribe({
      next: (profile) => {
        this.profiles.update((list) => [...list, profile]);
        this.selectProfile(profile.id);
        this.newProfileName = '';
        this.profileMsg.set(`Perfil "${profile.nombre}" creado`);
      },
      error: () => this.error.set('No se pudo crear el perfil')
    });
  }

  protected compartirPerfil(): void {
    const profile = this.selectedProfile();
    const email = this.shareEmail.trim();
    if (!profile) return;
    if (!email) {
      this.error.set('Indica el email del otro usuario');
      return;
    }
    this.profileService.share(profile.id, email).subscribe({
      next: (actualizado) => {
        this.profiles.update((list) => list.map((p) => (p.id === actualizado.id ? actualizado : p)));
        this.shareEmail = '';
        this.profileMsg.set(`Perfil compartido con ${email}`);
      },
      error: (err) => this.error.set(err?.error?.error ?? 'No se pudo compartir el perfil')
    });
  }

  protected openCapture(): void {
    this.showCapture.set(true);
    this.vision.set(null);
    this.captureName = '';
    this.captureColor = '';
    this.captureTipo = 'SUPERIOR';
    this.captureTemporada = 'Todo el año';
    this.captureTalla = '';
  }

  protected closeCapture(): void {
    this.showCapture.set(false);
  }

  protected onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;
    this.analyzing.set(true);
    this.visionService.analyze(file).subscribe({
      next: (result) => {
        this.vision.set(result);
        this.captureColor = result.colores[0] ?? '';
        this.captureTipo = result.tipoPrenda;
        this.analyzing.set(false);
      },
      error: () => {
        this.error.set('No se pudo analizar la foto');
        this.analyzing.set(false);
      }
    });
  }

  protected savePrenda(): void {
    const profileId = this.selectedProfileId();
    if (!profileId || !this.captureName.trim()) return;
    this.prendaService.create({
      profileId,
      nombre: this.captureName.trim(),
      color: this.captureColor,
      temporada: this.captureTemporada,
      talla: this.captureTalla,
      tipoPrenda: this.captureTipo,
      tejido: this.vision()?.tejido
    }).subscribe({
      next: (prenda) => {
        this.prendas.update((list) => [...list, prenda]);
        this.showCapture.set(false);
        if (this.activeTab() === 'ordenes') this.showOrdenes();
        else if (this.activeTab() === 'combinaciones') this.showCombinaciones();
      },
      error: () => this.error.set('No se pudo guardar la prenda')
    });
  }
}