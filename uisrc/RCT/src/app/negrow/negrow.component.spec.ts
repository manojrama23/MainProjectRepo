import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NegrowComponent } from './negrow.component';

describe('NegrowComponent', () => {
  let component: NegrowComponent;
  let fixture: ComponentFixture<NegrowComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NegrowComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NegrowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
