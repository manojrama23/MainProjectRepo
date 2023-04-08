package com.smart.rct.common.serviceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.CustomerEntity;
import com.smart.rct.common.entity.NetworkTypeDetailsEntity;
import com.smart.rct.common.models.NeCommissionModel;
import com.smart.rct.common.models.TrackLatiTudeModel;
import com.smart.rct.common.repository.CustomerRepository;
import com.smart.rct.common.repository.EnodeBViewMapRepository;
import com.smart.rct.common.repository.NetworkTypeDetailsRepository;
import com.smart.rct.common.service.EnodeBViewMapService;

@Service
public class EnodeBViewMapServiceImpl implements EnodeBViewMapService {

	final static Logger logger = LoggerFactory.getLogger(EnodeBViewMapServiceImpl.class);

	@Autowired
	EnodeBViewMapRepository objEnodeBViewMapRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	NetworkTypeDetailsRepository objNetworkTypeDetailsRepository;

	/**
	 * This api will save getMapEnodeBDetails
	 * 
	 * @param customerId
	 * @return Map<String, TrackLatiTudeModel>
	 */
	@Override
	public Map<String, TrackLatiTudeModel> getMapEnodeBDetails(Integer customerId) {
		Map<String, TrackLatiTudeModel> objMap = null;
		try {
			objMap = objEnodeBViewMapRepository.getMapEnodeBDetails(customerId);
		} catch (Exception e) {
			logger.error("Exception in EnodeBViewMapServiceImpl.getMapEnodeBDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objMap;
	}

	/**
	 * This api will save getNeCommissionData
	 * 
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getNeCommissionData() {
		// TODO Auto-generated method stub
		Map<String, Object> objMap = new HashedMap<>();
		List<NeCommissionModel> objNeCommissionModelList = new ArrayList<>();
		LinkedHashSet<String> objCustomersNames = new LinkedHashSet<>();
		try {
			List<CustomerEntity> objCustEntityList = customerRepository.getCustomerList(false, false);
			if (objCustEntityList != null && objCustEntityList.size() > 0) {
				objCustomersNames = objCustEntityList.stream().map(X -> X.getCustomerName())
						.collect(Collectors.toCollection(LinkedHashSet::new));
				List<NetworkTypeDetailsEntity> objNetworkTypeDetailsEntityList = objNetworkTypeDetailsRepository
						.getNwTypeDetails(false);
				LinkedHashSet<String> objNetWorkNames = objNetworkTypeDetailsEntityList.stream()
						.map(X -> X.getNetworkType()).collect(Collectors.toCollection(LinkedHashSet::new));
				Map<String, String> netWorkColrMap = objNetworkTypeDetailsEntityList.stream().collect(Collectors
						.toMap(NetworkTypeDetailsEntity::getNetworkType, NetworkTypeDetailsEntity::getNetworkColor));
				if (objNetWorkNames != null && objNetWorkNames.size() > 0) {
					Map<Integer, String> netWorkMap = objNetworkTypeDetailsEntityList.stream().collect(Collectors
							.toMap(NetworkTypeDetailsEntity::getId, NetworkTypeDetailsEntity::getNetworkType));
					for (String netWorkType : objNetWorkNames) {
						NeCommissionModel objModel = new NeCommissionModel();
						List<String> objList = new ArrayList<>();
						for (String objCustName : objCustomersNames) {
							for (CustomerEntity objCustomerEntity : objCustEntityList) {
								if (objCustName.equalsIgnoreCase(objCustomerEntity.getCustomerName())) {
									// int i = 0;
									// for MultiThreading purpose
									AtomicInteger counts = new AtomicInteger();
									List<CustomerDetailsEntity> objCustDetailsList = objCustomerEntity
											.getCustomerDetails();
									if (objCustDetailsList != null && objCustDetailsList.size() > 0) {
										for (CustomerDetailsEntity objCustomerDetailsEntity : objCustDetailsList) {
											if (netWorkMap.containsKey(
													objCustomerDetailsEntity.getNetworkTypeDetailsEntity().getId())
													&& netWorkType
															.equalsIgnoreCase(netWorkMap.get(objCustomerDetailsEntity
																	.getNetworkTypeDetailsEntity().getId()))) {
												// for one time for one customer related network
												if (counts.get() == 0) {
													objList.add("5");
													counts.getAndIncrement();
												}
											}
										}
									}
									if (counts.get() == 0) {
										objList.add("0");
									}
								}
							}
						}
						objModel.setBackgroundColor(netWorkColrMap.get(netWorkType));
						objModel.setLabel(netWorkType);
						objModel.setData(objList);
						objNeCommissionModelList.add(objModel);
					}
				}
			}
			objMap.put("labels", objCustomersNames);
			objMap.put("datasets", objNeCommissionModelList);
		} catch (Exception e) {
			objMap.put("labels", objCustomersNames);
			objMap.put("datasets", objNeCommissionModelList);
			logger.error("Exception in EnodeBViewMapServiceImpl.getNeCommissionData: "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objMap;
	}

	/**
	 * 
	 * this api will give the reason List and it has stub data
	 * 
	 * @return objMap
	 * 
	 */
	@Override
	public Map<String, Object> getReasonsChartData() {
		// TODO Auto-generated method stub
		Map<String, Object> objMap = new HashedMap<>();
		NeCommissionModel objModel = new NeCommissionModel();
		List<NeCommissionModel> objNeCommissionModelList = new ArrayList<>();
		String[] objCustomersNames = { "reason1", "reason2", "reason3", "reason4", "reason5" };
		List<String> data = Arrays.asList("580", "648", "560", "706", "866");
		objModel.setBackgroundColor("#5678ff");
		objModel.setLabel("");
		objModel.setData(data);
		objNeCommissionModelList.add(objModel);
		objMap.put("labels", objCustomersNames);
		objMap.put("datasets", objNeCommissionModelList);
		return objMap;
	}

	/**
	 * 
	 * this api will give the getRepChartData List and it has stub data
	 * 
	 * @return objMap
	 * 
	 */
	@Override
	public Map<String, Object> getRepChartData() {
		// TODO Auto-generated method stub
		Map<String, Object> objMap = new HashedMap<>();
		NeCommissionModel objModel = new NeCommissionModel();
		List<NeCommissionModel> objNeCommissionModelList = new ArrayList<>();
		String[] objCustomersNames = { "BRVZN 4G Legacy", "VZN-4G", "SPT-4G", "VZN-5G", "AT&T-4G" };
		List<String> data = Arrays.asList("580", "648", "560", "706", "866");
		objModel.setBackgroundColor("#108369");
		objModel.setLabel("");
		objModel.setData(data);
		objNeCommissionModelList.add(objModel);
		objMap.put("labels", objCustomersNames);
		objMap.put("datasets", objNeCommissionModelList);

		return objMap;
	}

}
