import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PreauditComponent } from './preaudit.component';

describe('PreauditComponent', () => {
  let component: PreauditComponent;
  let fixture: ComponentFixture<PreauditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PreauditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PreauditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
