package com.smart.rct.common.dto;

import java.util.Date;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.smart.rct.common.entity.LsmEntity;
import com.smart.rct.common.entity.NetworkTypeDetailsEntity;
import com.smart.rct.common.models.LsmModel;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;

@Component
public class LsmDetailsDto {

	final static Logger logger = LoggerFactory.getLogger(LsmDetailsDto.class);

	/**
	 * This method will set the LsmEntity with values from LsmModel.
	 * 
	 * @param objLsmModel
	 * @return objLsmEntity
	 */
	public LsmEntity getLsmEntity(LsmModel objLsmModel, String sessionId) {
		LsmEntity objLsmEntity = null;
		try {
			if (objLsmModel != null) {
				objLsmEntity = new LsmEntity();
				if (objLsmModel.getId() != null && objLsmModel.getId() != 0) {
					objLsmEntity.setId(Integer.valueOf(objLsmModel.getId()));
				}
				User user = UserSessionPool.getInstance().getSessionUser(sessionId);
				objLsmEntity.setCreatedBy(user.getUserName());
				objLsmEntity.setCreationDate(new Date());
				objLsmEntity.setLsmIp(objLsmModel.getLsmIp());
				objLsmEntity.setLsmName(objLsmModel.getLsmName());
				objLsmEntity.setLsmPassword(objLsmModel.getLsmPassword());
				objLsmEntity.setLsmUserName(objLsmModel.getLsmUserName());
				objLsmEntity.setRemarks(objLsmModel.getRemarks());
				objLsmEntity.setStatus(objLsmModel.getStatus());
				objLsmEntity.setLsmVersion(objLsmModel.getLsmVersion());
				objLsmEntity.setProgramName(objLsmModel.getProgramName());
				objLsmEntity.setNeType(objLsmModel.getNeType());
				objLsmEntity.setBucket(objLsmModel.getBucket());
				NetworkTypeDetailsEntity objNetworkTypeDetailsEntity = new NetworkTypeDetailsEntity();
				objNetworkTypeDetailsEntity.setId(objLsmModel.getNetworkTypeId());
				objLsmEntity.setNetworkTypeDetailsEntity(objNetworkTypeDetailsEntity);
			}
		} catch (Exception e) {
			logger.error("Excpetion in LsmDetailsDto.getLsmEntity(): " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return objLsmEntity;
	}
}
