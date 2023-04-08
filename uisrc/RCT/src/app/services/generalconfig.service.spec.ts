import { TestBed } from '@angular/core/testing';

import { GeneralconfigService } from './generalconfig.service';

describe('GeneralconfigService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: GeneralconfigService = TestBed.get(GeneralconfigService);
    expect(service).toBeTruthy();
  });
});
