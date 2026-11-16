import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {
  protected readonly mode = signal<'login' | 'register'>('login');
  protected readonly heroHidden = signal(false);
  protected nombre = '';
  protected email = '';
  protected password = '';
  protected error = signal('');
  protected loading = signal(false);

  constructor(private auth: AuthService) {}

  protected submit(): void {
    this.error.set('');
    this.loading.set(true);
    const action = this.mode() === 'login'
      ? this.auth.login(this.email, this.password)
      : this.auth.register(this.nombre, this.email, this.password);

    action.subscribe({
      next: () => this.loading.set(false),
      error: (err: any) => {
        this.loading.set(false);
        this.error.set(err?.error?.error ?? 'Error de autenticacion');
      }
    });
  }

  protected toggleMode(): void {
    this.mode.set(this.mode() === 'login' ? 'register' : 'login');
    this.error.set('');
  }
}
