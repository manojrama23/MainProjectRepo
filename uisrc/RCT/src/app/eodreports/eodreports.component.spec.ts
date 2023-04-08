import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EodreportsComponent } from './eodreports.component';

describe('EodreportsComponent', () => {
  let component: EodreportsComponent;
  let fixture: ComponentFixture<EodreportsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EodreportsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EodreportsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
