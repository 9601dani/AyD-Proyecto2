import { RouterModule, Routes } from '@angular/router';
import { TestComponentsComponent } from './test-components/test-components/test-components.component';
import { NgModule } from '@angular/core';
import { HomeComponent } from './components/commons/home/home.component';
import { LoginComponent } from './components/commons/login/login.component';
import { NotFoundComponent } from './components/commons/not-found/not-found.component';
import { CompanySettingsComponent } from './components/options/company-settings/company-settings.component';
import { ProfileComponent } from "./components/user/profile/profile.component";
import { VerificationEmailComponent } from './components/commons/verification-email/verification-email.component';
import { Verification2faComponent } from './components/commons/verification2fa/verification2fa.component';
import { AddServiceComponent } from './components/admin/add-service/add-service.component';
import { ViewServiceComponent } from './components/admin/view-service/view-service.component';
import { AddEmployeeComponent } from './components/admin/add-employee/add-employee.component';
import { AddResourceComponent } from './components/admin/add-resource/add-resource.component';
import { ViewResourceComponent } from './components/admin/view-resource/view-resource.component';
import { ViewEmployeeComponent } from './components/admin/view-employee/view-employee.component';
import { authGuard } from './guards/auth.guard';
import { RecoveryPasswordComponent } from './components/commons/recovery-password/recovery-password.component';
import { ResetPasswordComponent } from './components/commons/reset-password/reset-password.component';

export const routes: Routes = [

  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'test', component: TestComponentsComponent },
  { path: 'login', component: LoginComponent },
  { path: 'verify-email/:token', component: VerificationEmailComponent },
  { path: 'recovery-password', component: RecoveryPasswordComponent },
  { path: 'reset-password/:token', component: ResetPasswordComponent },
  { path: 'verify-2fa', component: Verification2faComponent, canActivate: [authGuard] },
  {
    path: 'options', children: [
      { path: 'company-settings', component: CompanySettingsComponent }
    ],
    canActivate: [authGuard]
  },
  {
    path: 'edit', children: [
      { path: 'profile', component: ProfileComponent }
    ],
    canActivate: [authGuard]
  },
  {
    path: 'services', children:[
      { path: 'add-service', component: AddServiceComponent },
      { path: 'edit-service/:id', component: AddServiceComponent },
      { path: 'show-services', component: ViewServiceComponent }
    ],
    canActivate: [authGuard]
  },
  {
    path: 'employees', children: [
      { path: 'add-employee', component: AddEmployeeComponent },
      { path: 'show-employee', component: ViewEmployeeComponent }
    ],
    canActivate: [authGuard]
  },
  {
    path: 'resources', children: [
      { path: 'add-resource', component: AddResourceComponent },
      { path: 'show-resources', component: ViewResourceComponent },
      { path: 'add-resource/:id', component: AddResourceComponent }
    ],
    canActivate: [authGuard]
  },
  { path: '**', component: NotFoundComponent }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})

export class AppRoutingModule { }
