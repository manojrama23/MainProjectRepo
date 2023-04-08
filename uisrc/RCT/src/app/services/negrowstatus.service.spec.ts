import { TestBed } from '@angular/core/testing';

import { NegrowstatusService } from './negrowstatus.service';

describe('NegrowstatusService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: NegrowstatusService = TestBed.get(NegrowstatusService);
    expect(service).toBeTruthy();
  });
});
