package com.smart.rct.premigration.service;

import java.util.List;
import java.util.Map;

import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.premigration.entity.NeConfigTypeEntity;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.models.NeMappingModel;

public interface NeMappingService {

	public Map<String, Object> getNeMapping(NeMappingModel neMappingModel, int page, int count);

	public Map<String,Map<String,List<NetworkConfigEntity>>> getDropDownList(Integer programId);

	public boolean saveNeMappingDetails(NeMappingEntity neMappingEntity);
	
	public List<NeMappingEntity> getNeMapping(NeMappingModel neMappingModel);
	
	public List<NeConfigTypeEntity> getNeConfigTypeDetails(Integer programId);

	public boolean saveNeConfigType(NeConfigTypeEntity entity);

	public List<NeMappingEntity>  getSiteName(String gnodebId);

	public List<NeMappingEntity> getGnodebs(String siteName);

	public List<NeMappingEntity> getGondebsByProgramName(NeMappingModel neMappingModel);
	
	public List<NetworkConfigEntity> getNeConfigList(Integer programId);

}
