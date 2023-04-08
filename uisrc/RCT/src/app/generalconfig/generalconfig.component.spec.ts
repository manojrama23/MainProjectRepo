import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GeneralconfigComponent } from './generalconfig.component';

describe('GeneralconfigComponent', () => {
  let component: GeneralconfigComponent;
  let fixture: ComponentFixture<GeneralconfigComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GeneralconfigComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GeneralconfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
