import { TestBed } from '@angular/core/testing';

import { PregrowService } from './pregrow.service';

describe('PregrowService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: PregrowService = TestBed.get(PregrowService);
    expect(service).toBeTruthy();
  });
});
