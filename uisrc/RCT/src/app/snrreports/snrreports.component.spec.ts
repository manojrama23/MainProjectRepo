import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SnrreportsComponent } from './snrreports.component';

describe('SnrreportsComponent', () => {
  let component: SnrreportsComponent;
  let fixture: ComponentFixture<SnrreportsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SnrreportsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SnrreportsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
