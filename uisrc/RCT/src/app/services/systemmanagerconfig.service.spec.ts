import { TestBed } from '@angular/core/testing';

import { SystemmanagerconfigService } from './systemmanagerconfig.service';

describe('SystemmanagerconfigService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: SystemmanagerconfigService = TestBed.get(SystemmanagerconfigService);
    expect(service).toBeTruthy();
  });
});
