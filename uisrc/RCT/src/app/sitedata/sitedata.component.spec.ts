import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SitedataComponent } from './sitedata.component';

describe('SitedataComponent', () => {
  let component: SitedataComponent;
  let fixture: ComponentFixture<SitedataComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SitedataComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SitedataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
