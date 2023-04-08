import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkflowmgmtComponent } from './workflowmgmt.component';

describe('WorkflowmgmtComponent', () => {
  let component: WorkflowmgmtComponent;
  let fixture: ComponentFixture<WorkflowmgmtComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WorkflowmgmtComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkflowmgmtComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
