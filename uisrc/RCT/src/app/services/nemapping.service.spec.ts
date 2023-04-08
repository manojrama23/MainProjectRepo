import { TestBed } from '@angular/core/testing';

import { NemappingService } from './nemapping.service';

describe('NemappingService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: NemappingService = TestBed.get(NemappingService);
    expect(service).toBeTruthy();
  });
});
