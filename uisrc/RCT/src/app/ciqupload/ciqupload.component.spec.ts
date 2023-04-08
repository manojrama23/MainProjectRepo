import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CiquploadComponent } from './ciqupload.component';

describe('CiquploadComponent', () => {
  let component: CiquploadComponent;
  let fixture: ComponentFixture<CiquploadComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CiquploadComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CiquploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
