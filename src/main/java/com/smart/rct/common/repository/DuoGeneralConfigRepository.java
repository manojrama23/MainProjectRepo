package com.smart.rct.common.repository;

import java.util.List;

import com.smart.rct.common.entity.DuoGeneralConfigEntity;

public interface DuoGeneralConfigRepository {

	public List<DuoGeneralConfigEntity> getDuoGeneralConfigList();

	public boolean saveDuoGeneralConfigEntity(DuoGeneralConfigEntity duoGeneralConfigEntity);

}
