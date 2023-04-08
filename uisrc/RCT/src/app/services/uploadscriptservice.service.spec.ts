import { TestBed } from '@angular/core/testing';

import { UploadscriptserviceService } from './uploadscriptservice.service';

describe('UploadscriptserviceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: UploadscriptserviceService = TestBed.get(UploadscriptserviceService);
    expect(service).toBeTruthy();
  });
});
