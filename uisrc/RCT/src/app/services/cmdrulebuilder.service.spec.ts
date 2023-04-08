import { TestBed } from '@angular/core/testing';

import { CmdrulebuilderService } from './cmdrulebuilder.service';

describe('CmdrulebuilderService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: CmdrulebuilderService = TestBed.get(CmdrulebuilderService);
    expect(service).toBeTruthy();
  });
});
