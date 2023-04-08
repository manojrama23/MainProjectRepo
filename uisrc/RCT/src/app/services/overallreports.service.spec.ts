import { TestBed } from '@angular/core/testing';

import { OverallreportsService } from './overallreports.service';

describe('OverallreportsService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: OverallreportsService = TestBed.get(OverallreportsService);
    expect(service).toBeTruthy();
  });
});
