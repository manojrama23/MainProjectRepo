package com.smart.rct.common.dto;

import java.util.Date;
import java.util.Random;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.smart.rct.common.entity.NetworkTypeDetailsEntity;
import com.smart.rct.common.models.NetworkTypeDetailsModel;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.usermanagement.repository.UserRoleDetailsRepository;

@Component
public class NetworkTypeDetailsDto {

	final static Logger logger = LoggerFactory.getLogger(NetworkTypeDetailsDto.class);

	@Autowired
	UserRoleDetailsRepository userRoleDetailsRepository;

	/**
	 * This method will set the NetworkTypeDetailsEntity with values from
	 * networkTypeDetailsModel.
	 * 
	 * @param networkTypeDetailsModel
	 * @return networkTypeDetailsEntity
	 */
	public NetworkTypeDetailsEntity getNwTypeDetails(NetworkTypeDetailsModel networkTypeDetailsModel,
			String sessionId) {

		NetworkTypeDetailsEntity networkTypeDetailsEntity = null;
		try {
			if (networkTypeDetailsModel != null) {
				networkTypeDetailsEntity = new NetworkTypeDetailsEntity();
				if (networkTypeDetailsModel.getId() != null && networkTypeDetailsModel.getId() != 0) {
					networkTypeDetailsEntity.setId(Integer.valueOf(networkTypeDetailsModel.getId()));
				}
				User user = UserSessionPool.getInstance().getSessionUser(sessionId);
				networkTypeDetailsEntity.setNetworkType(networkTypeDetailsModel.getNetworkType());
				networkTypeDetailsEntity.setCreatedBy(user.getUserName());
				networkTypeDetailsEntity.setCaretedDate(new Date());
				networkTypeDetailsEntity.setStatus(networkTypeDetailsModel.getStatus());
				networkTypeDetailsEntity.setRemarks(networkTypeDetailsModel.getRemarks());
				networkTypeDetailsEntity.setNetworkColor(String.format("#%06x", new Random().nextInt(256 * 256 * 256)));
			}
		} catch (Exception e) {
			logger.error("Excpetion NetworkTypeDetailsDto.getNwTypeEntity(): " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return networkTypeDetailsEntity;
	}
}
