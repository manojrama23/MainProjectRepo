package com.smart.rct.common.service;

import java.util.List;

import com.smart.rct.common.entity.DuoGeneralConfigEntity;
import com.smart.rct.common.models.ProgramTemplateModel;

public interface DuoGeneralConfigService {

	public List<ProgramTemplateModel> getDuoGeneralConfigList();

	public boolean saveDuoGeneralConfigEntity(DuoGeneralConfigEntity duoGeneralConfigEntity);

}
