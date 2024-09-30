import { RouterModule, Routes } from '@angular/router';
import { TestComponentsComponent } from './test-components/test-components/test-components.component';
import { NgModule } from '@angular/core';
import { HomeComponent } from './components/commons/home/home/home.component';

export const routes: Routes = [

    {path: '', redirectTo: 'home', pathMatch: 'full'},
    {path: 'home', component: HomeComponent},
    {path: 'test', component: TestComponentsComponent},

];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule],
})

export class AppRoutingModule {}