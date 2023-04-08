import { TestBed } from '@angular/core/testing';

import { SnrreportsService } from './snrreports.service';

describe('SnrreportsService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: SnrreportsService = TestBed.get(SnrreportsService);
    expect(service).toBeTruthy();
  });
});
