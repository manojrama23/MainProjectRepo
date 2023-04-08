import { TestBed } from '@angular/core/testing';

import { AuditpostService } from './auditpost.service';

describe('AuditpostService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: AuditpostService = TestBed.get(AuditpostService);
    expect(service).toBeTruthy();
  });
});
