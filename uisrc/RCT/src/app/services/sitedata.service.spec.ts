import { TestBed } from '@angular/core/testing';

import { SitedataService } from './sitedata.service';

describe('SitedataService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: SitedataService = TestBed.get(SitedataService);
    expect(service).toBeTruthy();
  });
});
