import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FilerulebuilderComponent } from './filerulebuilder.component';

describe('FilerulebuilderComponent', () => {
  let component: FilerulebuilderComponent;
  let fixture: ComponentFixture<FilerulebuilderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FilerulebuilderComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FilerulebuilderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
