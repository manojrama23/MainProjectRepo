import { TestBed } from '@angular/core/testing';

import { RanconfigService } from './ranconfig.service';

describe('RanconfigService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: RanconfigService = TestBed.get(RanconfigService);
    expect(service).toBeTruthy();
  });
});
