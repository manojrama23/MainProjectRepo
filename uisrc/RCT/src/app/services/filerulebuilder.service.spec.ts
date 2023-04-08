import { TestBed } from '@angular/core/testing';

import { FilerulebuilderService } from './filerulebuilder.service';

describe('FilerulebuilderService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: FilerulebuilderService = TestBed.get(FilerulebuilderService);
    expect(service).toBeTruthy();
  });
});
