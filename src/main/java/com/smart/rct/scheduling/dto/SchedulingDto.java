package com.smart.rct.scheduling.dto;

import java.util.Date;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.smart.rct.scheduling.entity.SchedulingEntity;
import com.smart.rct.scheduling.model.SchedulingModel;

@Component
public class SchedulingDto {
	final static Logger logger = LoggerFactory.getLogger(SchedulingDto.class);
	
	/**
	 * This method will set the SchedulingEntity with values from SchedulingModel.
	 * 
	 * @param objSchedulingModel
	 * @return objSchedulingEntity
	 */
	public SchedulingEntity getSchedulingEntity(SchedulingModel objSchedulingModel,String sessionId ) {

		SchedulingEntity objSchedulingEntity = null;
		try {
			if (objSchedulingModel != null) {
				objSchedulingEntity = new SchedulingEntity();
				if (objSchedulingModel.getId() != null && objSchedulingModel.getId() != 0) {
					objSchedulingEntity.setId(Integer.valueOf(objSchedulingModel.getId()));
				}
				objSchedulingEntity.setCommissioningName(objSchedulingModel.getCommissioningName());
				objSchedulingEntity.setNetworkType(objSchedulingModel.getNetworkType());
				objSchedulingEntity.setLsmVersion(objSchedulingModel.getLsmVersion());
				objSchedulingEntity.setLsmName(objSchedulingModel.getLsmName());
				objSchedulingEntity.setRemarks(objSchedulingModel.getRemarks());
				objSchedulingEntity.setPreMigFileName(objSchedulingModel.getPreMigFileName());
				objSchedulingEntity.setPreMigValidateDate(objSchedulingModel.getPreMigValidateDate());
				objSchedulingEntity.setPreMigGenerateDate(objSchedulingModel.getPreMigGenerateDate());
				objSchedulingEntity.setPreMigGrowDate(objSchedulingModel.getPreMigGrowDate());
				objSchedulingEntity.setMigUseCaseName(objSchedulingModel.getMigUseCaseName());
				objSchedulingEntity.setMigDate(objSchedulingModel.getMigDate());
				objSchedulingEntity.setPostMigUseCaseName(objSchedulingModel.getPostMigUseCaseName());
				objSchedulingEntity.setPostMigDate(objSchedulingModel.getPostMigDate());
			}
		} catch (Exception e) {
			logger.error("Excpetion getSchedulingEntity : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return objSchedulingEntity;
	}
}
