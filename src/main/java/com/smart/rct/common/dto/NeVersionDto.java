package com.smart.rct.common.dto;

import java.util.Date;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.smart.rct.common.entity.NeVersionEntity;
import com.smart.rct.common.models.NeVersionModel;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;

@Component
public class NeVersionDto {

	final static Logger logger = LoggerFactory.getLogger(NeVersionDto.class);

	/**
	 * This method will set the NeVersionEntity with values from NeVersionModel.
	 * 
	 * @param neVersionModel
	 * @return neVersionEntity
	 */
	public NeVersionEntity getNeVersionEntity(NeVersionModel neVersionModel, String sessionId) {
		NeVersionEntity neVersionEntity = null;
		try {
			if (neVersionModel != null) {
				neVersionEntity = new NeVersionEntity();
				neVersionEntity.setId(neVersionModel.getId());
				User user = UserSessionPool.getInstance().getSessionUser(sessionId);
				neVersionEntity.setProgramDetailsEntity(neVersionModel.getProgramDetailsEntity());
				neVersionEntity.setNeVersion(neVersionModel.getNeVersion());
				neVersionEntity.setStatus(neVersionModel.getStatus());
				neVersionEntity.setCreatedBy(user.getUserName());
				neVersionEntity.setCreationDate(new Date());
				neVersionEntity.setReleaseVersion(neVersionModel.getReleaseVersion());
			}
		} catch (Exception e) {
			logger.error("Excpetion getNetworkConfigEntity : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return neVersionEntity;
	}

}
