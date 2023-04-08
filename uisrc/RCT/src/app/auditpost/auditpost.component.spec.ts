import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AuditpostComponent } from './auditpost.component';

describe('AuditpostComponent', () => {
  let component: AuditpostComponent;
  let fixture: ComponentFixture<AuditpostComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AuditpostComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AuditpostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
