import { TestBed } from '@angular/core/testing';

import { RanatpService } from './ranatp.service';

describe('RanatpService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: RanatpService = TestBed.get(RanatpService);
    expect(service).toBeTruthy();
  });
});
