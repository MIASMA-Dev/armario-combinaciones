/**
 * Base de la API. En navegador se usan rutas relativas (proxy de Angular).
 * En Tauri/Capacitor el webview tiene otro origen, por lo que apunta al backend local.
 * Se puede sobrescribir con `localStorage.setItem('armario.apiBase', 'http://192.168.1.10:8080')`
 * para probar desde un movil en la red local.
 */
function esEntornoNativo(): boolean {
  const { protocol, hostname } = window.location;
  return (
    protocol === 'tauri:' ||
    protocol === 'capacitor:' ||
    hostname === 'tauri.localhost' ||
    (hostname === 'localhost' && protocol === 'https:')
  );
}

const override = (() => {
  try {
    return localStorage.getItem('armario.apiBase');
  } catch {
    return null;
  }
})();

const esAndroid = typeof navigator !== 'undefined' && /Android/i.test(navigator.userAgent);

// En el emulador Android el host se alcanza como 10.0.2.2. En iOS/Tauri, localhost.
const baseNativa = esAndroid ? 'http://10.0.2.2:8080' : 'http://localhost:8080';

export const API_BASE = (override ?? (esEntornoNativo() ? baseNativa : '')).replace(/\/$/, '');

export function apiUrl(path: string): string {
  return `${API_BASE}${path}`;
}
