import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from './_services/auth.service';
import { StorageService } from './_services/storage.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  private roles: string[] = [];
  isLoggedIn = false;
  username?: string;

  eventBusSub?: Subscription;

  constructor(
    private storageService: StorageService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.isLoggedIn = this.storageService.isLoggedIn();
    if (this.isLoggedIn) {
      const user = this.storageService.getUser();
      this.roles = user.roles;
      this.username = user.username;
    }
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: (data) => {
        console.debug(data);
      },
      error: (err) => {
        console.error(err);
      },
    });
    this.logoutHandler(null);
  }

  private logoutHandler(data: any): void {
    console.debug(data);
    this.isLoggedIn = false;
    this.storageService.clean();
    this.router.navigate(['/home']);
  }
}
