import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RanconfigComponent } from './ranconfig.component';

describe('RanconfigComponent', () => {
  let component: RanconfigComponent;
  let fixture: ComponentFixture<RanconfigComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RanconfigComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RanconfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
