import { TestBed } from '@angular/core/testing';

import { UsecaseService } from './usecase.service';

describe('UsecaseService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: UsecaseService = TestBed.get(UsecaseService);
    expect(service).toBeTruthy();
  });
});
