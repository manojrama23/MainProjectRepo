import { TestBed } from '@angular/core/testing';

import { RuntestService } from './runtest.service';

describe('RuntestService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: RuntestService = TestBed.get(RuntestService);
    expect(service).toBeTruthy();
  });
});
