import { RouterModule, Routes } from '@angular/router';
import { TestComponentsComponent } from './test-components/test-components/test-components.component';
import { NgModule } from '@angular/core';
import { HomeComponent } from './components/commons/home/home.component';
import { LoginComponent } from './components/commons/login/login.component';
import {ProfileComponent} from "./components/user/profile/profile.component";

export const routes: Routes = [

    {path: '', redirectTo: 'home', pathMatch: 'full'},
    {path: 'home', component: HomeComponent},
    {path: 'test', component: TestComponentsComponent},
    {path: 'login', component: LoginComponent},
    {
      path: 'edit', children: [
        {path: 'profile', component: ProfileComponent}
      ]
    }


];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule],
})

export class AppRoutingModule {}
