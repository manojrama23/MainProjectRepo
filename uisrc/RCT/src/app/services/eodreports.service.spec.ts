import { TestBed } from '@angular/core/testing';

import { EodreportsService } from './eodreports.service';

describe('EodreportsService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: EodreportsService = TestBed.get(EodreportsService);
    expect(service).toBeTruthy();
  });
});
