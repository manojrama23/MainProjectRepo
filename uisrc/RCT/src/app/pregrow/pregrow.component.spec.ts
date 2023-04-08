import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PregrowComponent } from './pregrow.component';

describe('PregrowComponent', () => {
  let component: PregrowComponent;
  let fixture: ComponentFixture<PregrowComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PregrowComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PregrowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
