import { TestBed } from '@angular/core/testing';

import { WorkflowmgmtService } from './workflowmgmt.service';

describe('WorkflowmgmtService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: WorkflowmgmtService = TestBed.get(WorkflowmgmtService);
    expect(service).toBeTruthy();
  });
});
