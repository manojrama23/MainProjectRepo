package com.smart.rct.premigration.dto;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.models.NeMappingModel;

@Component
public class NeMappingDto {
	final static Logger logger = LoggerFactory.getLogger(NeMappingDto.class);

	public NeMappingEntity getNetworkConfigEntity(NeMappingModel neMappingModel, String sessionId) {
		// TODO Auto-generated method stub
		NeMappingEntity neMappingEntity = null;
		try {
			if (neMappingModel != null) {
				neMappingEntity = new NeMappingEntity();
				if (neMappingModel.getId() != null && neMappingModel.getId() != 0) {
					neMappingEntity.setId(Integer.valueOf(neMappingModel.getId()));
				}
				
				neMappingEntity.setEnbId(neMappingModel.getEnbId());
				neMappingEntity.setProgramDetailsEntity(neMappingModel.getNetworkConfigEntity().getProgramDetailsEntity());
				neMappingEntity.setSiteConfigType(neMappingModel.getSiteConfigType());
				neMappingEntity.setNetworkConfigEntity(neMappingModel.getNetworkConfigEntity());
				neMappingEntity.setCreationDate(new Date());
				neMappingEntity.setEnbSbIp(neMappingModel.getEnbSbIp());
				neMappingEntity.setEnbSbVlan(neMappingModel.getEnbSbVlan());
				neMappingEntity.setBtsIp(neMappingModel.getBtsIp());
				neMappingEntity.setEnbOamIp(neMappingModel.getEnbOamIp());
				neMappingEntity.setEnbVlanId(neMappingModel.getEnbVlanId());
				neMappingEntity.setBtsId(neMappingModel.getBtsId());
				neMappingEntity.setBsmIp(neMappingModel.getBsmIp());
				neMappingEntity.setCiqName(neMappingModel.getCiqName());
			}
		}catch (Exception e) {
			logger.error("Excpetion getNetworkConfigEntity : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return neMappingEntity;
	}
	
	
	public NeMappingEntity getNetworkConfigEntity(NeMappingEntity neMappingEntity,NeMappingModel neMappingModel, String sessionId) {
		// TODO Auto-generated method stub
		try {
			if (neMappingModel != null) {
				if (neMappingModel.getId() != null && neMappingModel.getId() != 0) {
					neMappingEntity.setId(Integer.valueOf(neMappingModel.getId()));
				}
				
				neMappingEntity.setEnbId(neMappingModel.getEnbId());
				neMappingEntity.setProgramDetailsEntity(neMappingModel.getNetworkConfigEntity().getProgramDetailsEntity());
				neMappingEntity.setSiteConfigType(neMappingModel.getSiteConfigType());
				neMappingEntity.setNetworkConfigEntity(neMappingModel.getNetworkConfigEntity());
				neMappingEntity.setCreationDate(new Date());
				neMappingEntity.setEnbSbIp(neMappingModel.getEnbSbIp());
				neMappingEntity.setEnbSbVlan(neMappingModel.getEnbSbVlan());
				neMappingEntity.setBtsIp(neMappingModel.getBtsIp());
				neMappingEntity.setEnbOamIp(neMappingModel.getEnbOamIp());
				neMappingEntity.setEnbVlanId(neMappingModel.getEnbVlanId());
				neMappingEntity.setBtsId(neMappingModel.getBtsId());
				neMappingEntity.setBsmIp(neMappingModel.getBsmIp());
				neMappingEntity.setCiqName(neMappingModel.getCiqName());
			}
		}catch (Exception e) {
			logger.error("Excpetion getNetworkConfigEntity : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return neMappingEntity;
	}
	
	
}