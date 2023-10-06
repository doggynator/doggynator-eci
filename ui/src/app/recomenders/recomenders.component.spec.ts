import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecomendersComponent } from './recomenders.component';

describe('RecomendersComponent', () => {
  let component: RecomendersComponent;
  let fixture: ComponentFixture<RecomendersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RecomendersComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RecomendersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
