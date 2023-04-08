package com.smart.rct.premigration.repository;

import java.util.List;
import java.util.Map;

import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.premigration.entity.NeConfigTypeEntity;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.models.NeMappingModel;

public interface NeMappingRepository {

	public Map<String, Object> getNeMapping(NeMappingModel neMappingModel, int page, int count);

	public Map<String,Map<String,List<NetworkConfigEntity>>> getDropDownList(Integer programId);

	public boolean saveNeMappingDetails(NeMappingEntity neMappingEntity);
	
	public List<NeMappingEntity> getNeMapping(NeMappingModel neMappingModel);

	public List<NeConfigTypeEntity> getNeConfigTypeDetails(Integer programId);

	public boolean saveNeConfigType(NeConfigTypeEntity neConfigTypeEntity);

	public List<NeMappingEntity> getSName(String gnodebId);

	public List<NeMappingEntity> getGNBS(String gnodebId);


	List<NeMappingEntity> getNeMappingList(NeMappingModel neMappingModel);
	
	public List<NetworkConfigEntity> getNeConfigList(Integer programId);

}
