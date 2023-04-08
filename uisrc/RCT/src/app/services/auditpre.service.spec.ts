import { TestBed } from '@angular/core/testing';

import { AuditpreService } from './auditpre.service';

describe('AuditpreService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: AuditpreService = TestBed.get(AuditpreService);
    expect(service).toBeTruthy();
  });
});
