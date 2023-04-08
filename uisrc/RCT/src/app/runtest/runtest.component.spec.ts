import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RuntestComponent } from './runtest.component';

describe('RuntestComponent', () => {
  let component: RuntestComponent;
  let fixture: ComponentFixture<RuntestComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RuntestComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RuntestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
