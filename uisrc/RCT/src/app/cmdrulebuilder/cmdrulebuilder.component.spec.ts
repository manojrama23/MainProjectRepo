import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CmdrulebuilderComponent } from './cmdrulebuilder.component';

describe('CmdrulebuilderComponent', () => {
  let component: CmdrulebuilderComponent;
  let fixture: ComponentFixture<CmdrulebuilderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CmdrulebuilderComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CmdrulebuilderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
