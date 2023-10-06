import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CommingComponent } from './comming.component';

describe('CommingComponent', () => {
  let component: CommingComponent;
  let fixture: ComponentFixture<CommingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CommingComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CommingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
