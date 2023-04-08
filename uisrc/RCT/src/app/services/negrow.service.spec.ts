import { TestBed } from '@angular/core/testing';

import { NegrowService } from './negrow.service';

describe('NegrowService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: NegrowService = TestBed.get(NegrowService);
    expect(service).toBeTruthy();
  });
});
