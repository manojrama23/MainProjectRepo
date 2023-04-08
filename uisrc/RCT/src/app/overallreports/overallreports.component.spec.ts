import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OverallreportsComponent } from './overallreports.component';

describe('OverallreportsComponent', () => {
  let component: OverallreportsComponent;
  let fixture: ComponentFixture<OverallreportsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OverallreportsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OverallreportsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
