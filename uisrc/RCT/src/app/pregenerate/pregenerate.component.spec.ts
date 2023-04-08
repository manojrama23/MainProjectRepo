import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PregenerateComponent } from './pregenerate.component';

describe('PregenerateComponent', () => {
  let component: PregenerateComponent;
  let fixture: ComponentFixture<PregenerateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PregenerateComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PregenerateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
