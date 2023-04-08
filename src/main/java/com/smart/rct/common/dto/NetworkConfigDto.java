package com.smart.rct.common.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.smart.rct.common.entity.NetworkConfigDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.models.NetworkConfigDetailsModel;
import com.smart.rct.common.models.NetworkConfigModel;
import com.smart.rct.constants.Constants;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.CommonUtil;

@Component
public class NetworkConfigDto {

	final static Logger logger = LoggerFactory.getLogger(NetworkConfigDto.class);

	/**
	 * This method will set the NetworkConfigEntity with values from
	 * NetworkConfigModel.
	 * 
	 * @param networkConfigModel
	 * @return networkConfigEntity
	 */
	public NetworkConfigEntity getNetworkConfigEntity(NetworkConfigModel networkConfigModel, String sessionId) {
		NetworkConfigEntity networkConfigEntity = null;
		try {
			if (networkConfigModel != null) {
				networkConfigEntity = new NetworkConfigEntity();
				if (networkConfigModel.getId() != null && networkConfigModel.getId() != 0) {
					networkConfigEntity.setId(Integer.valueOf(networkConfigModel.getId()));
				}
				User user = UserSessionPool.getInstance().getSessionUser(sessionId);
				networkConfigEntity.setCreatedBy(user.getUserName());
				networkConfigEntity.setCreationDate(new Date());
				networkConfigEntity.setNeIp(networkConfigModel.getNeIp());
				networkConfigEntity.setNeName(networkConfigModel.getNeName());
				networkConfigEntity.setNePassword(networkConfigModel.getNePassword());
				networkConfigEntity.setNeUserName(networkConfigModel.getNeUserName());
				networkConfigEntity.setRemarks(networkConfigModel.getRemarks());
				networkConfigEntity.setStatus(networkConfigModel.getStatus());
				networkConfigEntity.setNeVersionEntity(networkConfigModel.getNeVersionEntity());
				networkConfigEntity.setProgramDetailsEntity(networkConfigModel.getProgramDetailsEntity());
				networkConfigEntity.setNeTypeEntity(networkConfigModel.getNeTypeEntity());
				networkConfigEntity.setLoginTypeEntity(networkConfigModel.getLoginTypeEntity());
				networkConfigEntity.setNeRsIp(networkConfigModel.getNeRsIp());
				networkConfigEntity.setNeMarket(networkConfigModel.getNeMarket());
				networkConfigEntity.setNeUserPrompt(networkConfigModel.getNeUserPrompt());
				networkConfigEntity.setNeSuperUserPrompt(networkConfigModel.getNeSuperUserPrompt());
				networkConfigEntity.setNeRelVersion(networkConfigModel.getNeRelVersion());
				List<NetworkConfigDetailsEntity> neDetails = new ArrayList<NetworkConfigDetailsEntity>();
				if (CommonUtil.isValidObject(networkConfigModel.getNeDetails())
						&& networkConfigModel.getNeDetails().size() > 0) {
					NetworkConfigDetailsEntity networkConfigDetailsEntity = new NetworkConfigDetailsEntity();
					for (NetworkConfigDetailsModel neDetailsModel : networkConfigModel.getNeDetails()) {
						networkConfigDetailsEntity = getNetworkConfigDetailEntity(neDetailsModel);
						networkConfigDetailsEntity.setNetworkConfigEntity(networkConfigEntity);
						networkConfigDetailsEntity.setCreatedBy(user.getUserName());
						networkConfigDetailsEntity.setCreationDate(new Date());
						neDetails.add(networkConfigDetailsEntity);
					}
				}
				networkConfigEntity.setNeDetails(neDetails);
			}
		} catch (Exception e) {
			logger.error("Excpetion getNetworkConfigEntity : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return networkConfigEntity;
	}

	/**
	 * This method will set the NetworkConfigDetailsEntity with values from
	 * NetworkConfigDetailsModel.
	 * 
	 * @param networkConfigDetailsModel
	 * @return networkConfigDetailsEntity
	 */
	public NetworkConfigDetailsEntity getNetworkConfigDetailEntity(
			NetworkConfigDetailsModel networkConfigDetailsModel) {
		NetworkConfigDetailsEntity networkConfigDetailsEntity = null;
		try {
			networkConfigDetailsEntity = new NetworkConfigDetailsEntity();
			networkConfigDetailsEntity.setId(networkConfigDetailsModel.getId());
			networkConfigDetailsEntity.setStep(networkConfigDetailsModel.getStep());
			networkConfigDetailsEntity.setServerTypeEntity(networkConfigDetailsModel.getServerTypeEntity());
			networkConfigDetailsEntity.setServerName(networkConfigDetailsModel.getServerName());
			networkConfigDetailsEntity.setServerIp(networkConfigDetailsModel.getServerIp());
			networkConfigDetailsEntity.setServerUserName(networkConfigDetailsModel.getServerUserName());
			networkConfigDetailsEntity.setServerPassword(networkConfigDetailsModel.getServerPassword());
			networkConfigDetailsEntity.setLoginTypeEntity(networkConfigDetailsModel.getLoginTypeEntity());
			networkConfigDetailsEntity.setPath(networkConfigDetailsModel.getPath());
			networkConfigDetailsEntity.setUserPrompt(networkConfigDetailsModel.getUserPrompt());
			networkConfigDetailsEntity.setSuperUserPrompt(networkConfigDetailsModel.getSuperUserPrompt());
			networkConfigDetailsEntity.setCreatedBy(networkConfigDetailsModel.getCreatedBy());
			networkConfigDetailsEntity.setCreationDate(new Date());
			NetworkConfigEntity objNetworkConfigEntity = networkConfigDetailsModel.getNetworkConfigEntity();
			networkConfigDetailsEntity.setNetworkConfigEntity(objNetworkConfigEntity);
		} catch (Exception e) {
			logger.error("Excpetion getNetworkConfigDetailEntity : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return networkConfigDetailsEntity;
	}

	/**
	 * This method will set the NetworkConfigModel with values from
	 * NetworkConfigEntity.
	 * 
	 * @param networkConfigEntity
	 * @return networkConfigModel
	 */
	public NetworkConfigModel getNetworkConfigModel(NetworkConfigEntity networkConfigEntity) {
		NetworkConfigModel networkConfigModel = null;
		try {
			if (networkConfigEntity != null) {
				networkConfigModel = new NetworkConfigModel();
				if (networkConfigEntity.getId() != null && networkConfigEntity.getId() != 0) {
					networkConfigModel.setId(Integer.valueOf(networkConfigEntity.getId()));
				}
				networkConfigModel.setCreatedBy(networkConfigEntity.getCreatedBy());
				if (CommonUtil.isValidObject(networkConfigEntity.getCreationDate())) {
					networkConfigModel.setCreationDate(
							CommonUtil.dateToString(networkConfigEntity.getCreationDate(), Constants.YYYY_MM_DD_HH_MM));
				}
				networkConfigModel.setNeIp(networkConfigEntity.getNeIp());
				networkConfigModel.setNeName(networkConfigEntity.getNeName());
				networkConfigModel.setNePassword(networkConfigEntity.getNePassword());
				networkConfigModel.setNeUserName(networkConfigEntity.getNeUserName());
				networkConfigModel.setRemarks(networkConfigEntity.getRemarks());
				networkConfigModel.setStatus(networkConfigEntity.getStatus());
				networkConfigModel.setNeVersionEntity(networkConfigEntity.getNeVersionEntity());
				networkConfigModel.setProgramDetailsEntity(networkConfigEntity.getProgramDetailsEntity());
				networkConfigModel.setNeTypeEntity(networkConfigEntity.getNeTypeEntity());
				networkConfigModel.setLoginTypeEntity(networkConfigEntity.getLoginTypeEntity());
				networkConfigModel.setNeRsIp(networkConfigEntity.getNeRsIp());
				networkConfigModel.setNeMarket(networkConfigEntity.getNeMarket());
				networkConfigModel.setNeUserPrompt(networkConfigEntity.getNeUserPrompt());
				networkConfigModel.setNeSuperUserPrompt(networkConfigEntity.getNeSuperUserPrompt());
				networkConfigModel.setNeRelVersion(networkConfigEntity.getNeRelVersion());
				List<NetworkConfigDetailsModel> neDetails = new ArrayList<NetworkConfigDetailsModel>();
				if (CommonUtil.isValidObject(networkConfigEntity.getNeDetails())
						&& networkConfigEntity.getNeDetails().size() > 0) {
					NetworkConfigDetailsModel networkConfigDetailsModel = new NetworkConfigDetailsModel();
					for (NetworkConfigDetailsEntity neDetailsEntity : networkConfigEntity.getNeDetails()) {
						networkConfigDetailsModel = getNetworkConfigDetailModel(neDetailsEntity);
						networkConfigDetailsModel.setCreatedBy(networkConfigEntity.getCreatedBy());
						if (CommonUtil.isValidObject(networkConfigEntity.getCreationDate())) {
							networkConfigDetailsModel.setCreationDate(CommonUtil
									.dateToString(networkConfigEntity.getCreationDate(), Constants.YYYY_MM_DD_HH_MM));
						}
						neDetails.add(networkConfigDetailsModel);
					}
				}
				networkConfigModel.setNeDetails(neDetails);
			}
		} catch (Exception e) {
			logger.error("Excpetion getNetworkConfigModel : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return networkConfigModel;
	}

	/**
	 * This method will set the NetworkConfigDetailsModel with values from
	 * NetworkConfigDetailsEntity.
	 * 
	 * @param networkConfigDetailsEntity
	 * @return networkConfigDetailsModel
	 */
	public NetworkConfigDetailsModel getNetworkConfigDetailModel(
			NetworkConfigDetailsEntity networkConfigDetailsEntity) {
		NetworkConfigDetailsModel networkConfigDetailsModel = new NetworkConfigDetailsModel();
		try {
			networkConfigDetailsModel = new NetworkConfigDetailsModel();
			networkConfigDetailsModel.setId(networkConfigDetailsEntity.getId());
			networkConfigDetailsModel.setStep(networkConfigDetailsEntity.getStep());
			networkConfigDetailsModel.setServerTypeEntity(networkConfigDetailsEntity.getServerTypeEntity());
			networkConfigDetailsModel.setServerName(networkConfigDetailsEntity.getServerName());
			networkConfigDetailsModel.setServerIp(networkConfigDetailsEntity.getServerIp());
			networkConfigDetailsModel.setServerUserName(networkConfigDetailsEntity.getServerUserName());
			networkConfigDetailsModel.setServerPassword(networkConfigDetailsEntity.getServerPassword());
			networkConfigDetailsModel.setLoginTypeEntity(networkConfigDetailsEntity.getLoginTypeEntity());
			networkConfigDetailsModel.setPath(networkConfigDetailsEntity.getPath());
			networkConfigDetailsModel.setUserPrompt(networkConfigDetailsEntity.getUserPrompt());
			networkConfigDetailsModel.setSuperUserPrompt(networkConfigDetailsEntity.getSuperUserPrompt());
			
		} catch (Exception e) {
			logger.error("Excpetion getNetworkConfigDetailModel : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return networkConfigDetailsModel;
	}

}