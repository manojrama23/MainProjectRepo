import { TestBed } from '@angular/core/testing';

import { CiquploadService } from './ciqupload.service';

describe('CiquploadService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: CiquploadService = TestBed.get(CiquploadService);
    expect(service).toBeTruthy();
  });
});
