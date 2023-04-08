import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RanatpComponent } from './ranatp.component';

describe('RanatpComponent', () => {
  let component: RanatpComponent;
  let fixture: ComponentFixture<RanatpComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RanatpComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RanatpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
