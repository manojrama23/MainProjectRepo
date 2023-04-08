import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NestatusComponent } from './nestatus.component';

describe('NestatusComponent', () => {
  let component: NestatusComponent;
  let fixture: ComponentFixture<NestatusComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NestatusComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NestatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
