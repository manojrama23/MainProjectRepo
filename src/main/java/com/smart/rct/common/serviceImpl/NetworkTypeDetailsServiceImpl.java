package com.smart.rct.common.serviceImpl;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.NetworkTypeDetailsEntity;
import com.smart.rct.common.repository.NetworkTypeDetailsRepository;
import com.smart.rct.common.service.NetworkTypeDetailsService;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.exception.RctException;
import com.smart.rct.interceptor.GlobalInitializerListener;

@Service
public class NetworkTypeDetailsServiceImpl implements NetworkTypeDetailsService {

	private static final Logger logger = LoggerFactory.getLogger(NetworkTypeDetailsServiceImpl.class);

	@Autowired
	NetworkTypeDetailsRepository networkTypeDetailsRepository;

	/**
	 * This API Add NetworkTypeDetails
	 * 
	 * @param NetworkTypeDetailsEntity
	 * @return boolean
	 */
	@Override
	public boolean saveNetworkTypeDetails(NetworkTypeDetailsEntity networkTypeDetailsEntity) {
		boolean status = false;
		try {
			status = networkTypeDetailsRepository.saveNetworkTypeDetails(networkTypeDetailsEntity);
		} catch (Exception e) {
			status = false;
			logger.error("Exception in NetworkTypeDetailsServiceImpl.creatNetworkType(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This api updateNetworkTypeDetails
	 * 
	 * @param NetworkTypeDetailsEntity
	 * @return boolean
	 */
	@Override
	public List<NetworkTypeDetailsEntity> getNwTypeDetails(boolean addAllRecord) {
		List<NetworkTypeDetailsEntity> nwTypeEntityList = null;
		try {
			nwTypeEntityList = networkTypeDetailsRepository.getNwTypeDetails(addAllRecord);
		} catch (Exception e) {
			logger.error("Exception in NetworkTypeDetailsServiceImpl.getNwTypeDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return nwTypeEntityList;
	}

	/**
	 * This api updateNetworkTypeDetails
	 * 
	 * @param NetworkTypeDetailsEntity
	 * @return boolean
	 * @throws RctException
	 */
	@Override
	public boolean deleteNetworkTypeDetials(int nwTypeId) throws RctException {
		boolean status = false;

		try {
			status = networkTypeDetailsRepository.deleteNetworkTypeDetials(nwTypeId);
		} catch (Exception e) {
			if (e instanceof DataIntegrityViolationException) {
				throw new RctException(
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.NETWORK_TYPE_DETAILS_ASSOSIATED));
			}
			logger.error("Exception  in NetworkTypeDetailsServiceImpl.deleteNwtypeDetails():"
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return status;
	}

	/**
	 * This api getNetworkTypeById
	 * 
	 * @param networkTypeId
	 * @return NetworkTypeDetailsEntity
	 */
	@Override
	public NetworkTypeDetailsEntity getNetworkTypeById(int networkTypeId) {
		NetworkTypeDetailsEntity networkTypeDetailsEntity = null;
		try {
			networkTypeDetailsEntity = networkTypeDetailsRepository.getNetworkTypeById(networkTypeId);
		} catch (Exception e) {
			logger.error("Exception in NetworkTypeDetailsServiceImpl.getNetworkTypeById: "
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return networkTypeDetailsEntity;
	}

	/**
	 * This api getNwTypeDetailsByCustomerId
	 * 
	 * @param customerId
	 * @return CustomerDetailsEntity
	 */
	@Override
	public List<CustomerDetailsEntity> getNwTypeDetailsByCustomerId(int customerId) {
		List<CustomerDetailsEntity> customerDetailsEntities = null;
		try {
			customerDetailsEntities = networkTypeDetailsRepository.getNwTypeDetailsByCustomerId(customerId);
		} catch (Exception e) {
			logger.error("Exception in NetworkTypeDetailsServiceImpl.getNwTypeDetailsByCustomerId: "
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return customerDetailsEntities;
	}

	/**
	 * This api getNetworkTypeByName
	 * 
	 * @param networkType
	 * @return NetworkTypeDetailsEntity
	 */
	@Override
	public NetworkTypeDetailsEntity getNetworkTypeByName(String networkType) {
		NetworkTypeDetailsEntity networkTypeDetailsEntity = null;
		try {
			networkTypeDetailsEntity = networkTypeDetailsRepository.getNetworkTypeByName(networkType);
		} catch (Exception e) {
			logger.error("Exception in NetworkTypeDetailsServiceImpl.getNetworkTypeByName: "
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return networkTypeDetailsEntity;
	}

}
