import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RolesAndPrivilegesComponent } from './roles-and-privileges.component';

describe('RolesAndPrivilegesComponent', () => {
  let component: RolesAndPrivilegesComponent;
  let fixture: ComponentFixture<RolesAndPrivilegesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RolesAndPrivilegesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RolesAndPrivilegesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
