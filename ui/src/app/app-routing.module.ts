import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { RegisterComponent } from './register/register.component';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { ProfileComponent } from './profile/profile.component';
import { BoardUserComponent } from './board-user/board-user.component';
import { BoardModeratorComponent } from './board-moderator/board-moderator.component';
import { BoardAdminComponent } from './board-admin/board-admin.component';
import { SettingsComponent } from './settings/settings.component';
import { CommingComponent } from './comming/comming.component';
import { CardsComponent } from './cards/cards.component';
import { authGuard } from './_helpers/auth.guard';
import { FaqsComponent } from './faqs/faqs.component';
import { RecomendersComponent } from './recomenders/recomenders.component';
import { LoadingComponent } from './loading/loading.component';


const routes: Routes = [
  { title: "Inicio", path: 'home', component: HomeComponent },
  { title: "Ingreso", path: 'login', component: LoginComponent },
  { title: "Registro", path: 'register', component: RegisterComponent },
  { title: "Perfil", path: 'profile', component: ProfileComponent, data:{requiresLogin: true}, canActivate: [authGuard] },
  { title: "Usuario", path: 'user', component: BoardUserComponent, data:{requiresLogin: true}, canActivate: [authGuard] },
  { title: "Moderador", path: 'mod', component: BoardModeratorComponent, data:{requiresLogin: true}, canActivate: [authGuard] },
  { title: "Administrador", path: 'admin', component: BoardAdminComponent, data:{requiresLogin: true}, canActivate: [authGuard] },
  { title: "Perfilamiento", path: 'settings', component: SettingsComponent, data:{requiresLogin: true}, canActivate: [authGuard] },
  { title: "TODO:", path: 'comming', component: CommingComponent, data:{requiresLogin: true}, canActivate: [authGuard] },
  { title: "Recomendacion", path: 'cards', component: CardsComponent, data:{requiresLogin: true}, canActivate: [authGuard] },
  { title: "FAQs", path: 'faqs', component: FaqsComponent },
  { title: "Recomendaciones", path: 'recomenders', component: RecomendersComponent, data:{requiresLogin: true}, canActivate: [authGuard] },
  { title: "Loading", path: 'loading', component: LoadingComponent, data:{requiresLogin: true}, canActivate: [authGuard] },
  { path: '', redirectTo: 'home', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
