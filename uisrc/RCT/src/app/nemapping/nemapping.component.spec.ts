import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NemappingComponent } from './nemapping.component';

describe('NemappingComponent', () => {
  let component: NemappingComponent;
  let fixture: ComponentFixture<NemappingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NemappingComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NemappingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
