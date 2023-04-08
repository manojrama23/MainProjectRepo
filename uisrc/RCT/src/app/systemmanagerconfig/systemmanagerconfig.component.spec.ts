import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SystemmanagerconfigComponent } from './systemmanagerconfig.component';

describe('SystemmanagerconfigComponent', () => {
  let component: SystemmanagerconfigComponent;
  let fixture: ComponentFixture<SystemmanagerconfigComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SystemmanagerconfigComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SystemmanagerconfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
