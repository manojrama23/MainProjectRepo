package com.smart.rct.premigration.serviceImpl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.premigration.entity.NeConfigTypeEntity;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.models.NeMappingModel;
import com.smart.rct.premigration.repository.NeMappingRepository;
import com.smart.rct.premigration.service.NeMappingService;

@Service
public class NeMappingServiceImpl implements NeMappingService{

	final static Logger logger = LoggerFactory.getLogger(NeMappingServiceImpl.class);

	@Autowired
	NeMappingRepository neMappingRepository;
	
	
	@Override
	public Map<String, Object> getNeMapping(NeMappingModel neMappingModel, int page, int count) {
		// TODO Auto-generated method stub
		Map<String, Object> objMap = null;
		try {
			objMap = neMappingRepository.getNeMapping(neMappingModel, page, count);

		} catch (Exception e) {
			// TODO: handle exception
			logger.error("getNeMapping() NeMappingServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		}

		return objMap;

	}




	@Override
	public Map<String,Map<String,List<NetworkConfigEntity>>> getDropDownList(Integer programId) {
		Map<String,Map<String,List<NetworkConfigEntity>>> marketList = null;
		try {
			marketList = neMappingRepository.getDropDownList(programId);

		} catch (Exception e) {
			logger.error(" getMarketDetails service : " + ExceptionUtils.getFullStackTrace(e));
		}

		return marketList;
	}





	@Override
	public boolean saveNeMappingDetails(NeMappingEntity neMappingEntity) {
		boolean status = false;
		try {
			status = neMappingRepository.saveNeMappingDetails(neMappingEntity);
		} catch (Exception e) {
			status = false;
			logger.error("Exception NeMappingServiceImpl.saveNeMappingDetails() : " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}




	@Override
	public List<NeMappingEntity> getNeMapping(NeMappingModel neMappingModel) {
		List<NeMappingEntity> neMappingEntityList = null;
		try {
			neMappingEntityList = neMappingRepository.getNeMapping(neMappingModel);
		} catch (Exception e) {
			logger.error("Exception NeMappingServiceImpl.getNeMapping() : " + ExceptionUtils.getFullStackTrace(e));
		}
		return neMappingEntityList;
	}




	@Override
	public List<NeConfigTypeEntity> getNeConfigTypeDetails(Integer programId) {
		List<NeConfigTypeEntity> neConfigTypeList = null;
		try {
			neConfigTypeList = neMappingRepository.getNeConfigTypeDetails(programId);
		} catch (Exception e) {
			logger.error("Exception NeMappingServiceImpl.getNeConfigTypeDetails() : " + ExceptionUtils.getFullStackTrace(e));
		}
		return neConfigTypeList;
	}




	@Override
	public boolean saveNeConfigType(NeConfigTypeEntity neConfigTypeEntity) {
		boolean status = false;
		try {
			status = neMappingRepository.saveNeConfigType(neConfigTypeEntity);
		} catch (Exception e) {
			status = false;
			logger.error("Exception saveNeConfigType.neConfigTypeEntity() : " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}




	@Override
	public List<NeMappingEntity> getSiteName(String gnodebId) {
		// TODO Auto-generated method stu
		List<NeMappingEntity> neMappingEntity = null;;
		try {
			neMappingEntity = neMappingRepository.getSName(gnodebId);
		} catch (Exception e) {
			logger.error("Exception saveNeConfigType.neConfigTypeEntity() : " + ExceptionUtils.getFullStackTrace(e));
		}
		return neMappingEntity;
		
		
	}




	@Override
	public List<NeMappingEntity> getGnodebs(String siteName) {
		// TODO Auto-generated method stub
		
		List<NeMappingEntity> neMappingEntity = null;;
		try {
			neMappingEntity = neMappingRepository.getGNBS(siteName);
		} catch (Exception e) {
			logger.error("Exception saveNeConfigType.neConfigTypeEntity() : " + ExceptionUtils.getFullStackTrace(e));
		}
		return neMappingEntity;
	}




	@Override
	public List<NeMappingEntity> getGondebsByProgramName(NeMappingModel neMap) {
		// TODO Auto-generated method stub
		
		
		List<NeMappingEntity> neMappingEntity = null;
		try {
			neMappingEntity=	neMappingRepository.getNeMappingList(neMap);
		} catch (Exception e) {
			logger.error("Exception saveNeConfigType.neConfigTypeEntity() : " + ExceptionUtils.getFullStackTrace(e));
		}
		return neMappingEntity;
		
	}
	
	@Override
	public List<NetworkConfigEntity> getNeConfigList(Integer programId) {
		List<NetworkConfigEntity> marketList = null;
		try {
			marketList = neMappingRepository.getNeConfigList(programId);

		} catch (Exception e) {
			logger.error(" getNeConfigList service : " + ExceptionUtils.getFullStackTrace(e));
		}

		return marketList;
	}

}
