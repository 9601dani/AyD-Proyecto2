import { RouterModule, Routes } from '@angular/router';
import { TestComponentsComponent } from './test-components/test-components/test-components.component';
import { NgModule } from '@angular/core';
import { HomeComponent } from './components/commons/home/home.component';
import { LoginComponent } from './components/commons/login/login.component';
import { NotFoundComponent } from './components/commons/not-found/not-found.component';
import { CompanySettingsComponent } from './components/options/company-settings/company-settings.component';
import { ProfileComponent } from "./components/user/profile/profile.component";

export const routes: Routes = [

  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'test', component: TestComponentsComponent },
  { path: 'login', component: LoginComponent },
  {
    path: 'options', children: [
      { path: 'company-settings', component: CompanySettingsComponent }
    ]
  },
  {
    path: 'edit', children: [
      { path: 'profile', component: ProfileComponent }
    ]
  },
  { path: '**', component: NotFoundComponent }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})

export class AppRoutingModule { }
