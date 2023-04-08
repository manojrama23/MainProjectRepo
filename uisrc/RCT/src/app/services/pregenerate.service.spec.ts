import { TestBed } from '@angular/core/testing';

import { PregenerateService } from './pregenerate.service';

describe('PregenerateService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: PregenerateService = TestBed.get(PregenerateService);
    expect(service).toBeTruthy();
  });
});
