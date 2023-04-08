package com.smart.rct.premigration.repository;

import java.util.List;

import com.smart.rct.common.entity.GrowConstantsEntity;
import com.smart.rct.common.models.GenerateConstantsModel;

public interface GrowConstantsRepository {
	
	public List<GrowConstantsEntity> getGrowConstantsDetails();
	public List<GrowConstantsEntity> getGrowConstantsDetails(GenerateConstantsModel generateConstantsModel);
}
