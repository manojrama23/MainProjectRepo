import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UsecasebuilderComponent } from './usecasebuilder.component';

describe('UsecasebuilderComponent', () => {
  let component: UsecasebuilderComponent;
  let fixture: ComponentFixture<UsecasebuilderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UsecasebuilderComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UsecasebuilderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
