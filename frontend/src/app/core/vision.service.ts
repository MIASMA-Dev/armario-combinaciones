import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { apiUrl } from './api.config';

export interface VisionResult {
  colores: string[];
  tipoPrenda: string;
  tejido: string;
  confianza: number;
}

@Injectable({ providedIn: 'root' })
export class VisionService {
  constructor(private http: HttpClient) {}

  analyze(file: File): Observable<VisionResult> {
    const form = new FormData();
    form.append('file', file);
    return this.http.post<VisionResult>(apiUrl('/api/vision/analyze'), form);
  }
}
