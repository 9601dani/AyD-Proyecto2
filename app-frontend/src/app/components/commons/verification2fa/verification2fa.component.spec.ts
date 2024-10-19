import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Verification2faComponent } from './verification2fa.component';

describe('Verification2faComponent', () => {
  let component: Verification2faComponent;
  let fixture: ComponentFixture<Verification2faComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Verification2faComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Verification2faComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
