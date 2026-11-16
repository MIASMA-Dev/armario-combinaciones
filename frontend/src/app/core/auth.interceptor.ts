import { HttpInterceptorFn } from '@angular/common/http';
import { API_BASE } from './api.config';

const TOKEN_KEY = 'armario.token';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  let token: string | null = null;
  try {
    token = localStorage.getItem(TOKEN_KEY);
  } catch {
    token = null;
  }

  // Adjunta el token solo a peticiones de nuestra API.
  const esApi = API_BASE ? req.url.startsWith(API_BASE) : req.url.startsWith('/api');
  const authReq = token && esApi
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authReq);
};
