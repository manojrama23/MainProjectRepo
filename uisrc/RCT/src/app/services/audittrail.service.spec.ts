import { TestBed } from '@angular/core/testing';

import { AudittrailService } from './audittrail.service';

describe('AudittrailService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: AudittrailService = TestBed.get(AudittrailService);
    expect(service).toBeTruthy();
  });
});
