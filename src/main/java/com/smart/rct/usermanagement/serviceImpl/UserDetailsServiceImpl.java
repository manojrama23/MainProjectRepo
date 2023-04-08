package com.smart.rct.usermanagement.serviceImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.CustomerEntity;
import com.smart.rct.constants.Constants;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.postmigration.models.SchedulingSRModel;
import com.smart.rct.postmigration.repository.SchedulingSRRepository;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.models.DashBoardModel;
import com.smart.rct.premigration.models.NeMappingModel;
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.premigration.service.NeMappingService;
import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.usermanagement.models.UserDetailsModel;
import com.smart.rct.usermanagement.repository.UserDetailsRepository;
import com.smart.rct.usermanagement.service.UserDetailsService;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;
import com.smart.rct.util.GlobalStatusMap;
import com.smart.rct.util.PasswordCrypt;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

	@Autowired
	UserDetailsRepository objUserDetailsRepository;
	
	@Autowired
	SchedulingSRRepository schedulingSRRepository;
	
	@Autowired
	FileUploadRepository fileUploadRepository;
	
	@Autowired
	NeMappingService neMappingService;

	/**
	 * 
	 * this method will create the new user
	 * 
	 * @param userEntity
	 * @return boolean
	 */
	@Override
	public boolean createUser(UserDetailsEntity userEntity) {
		boolean status = false;
		try {
			String dateString = DateUtil.dateToString(new Date(), Constants.YYYY_MM_DD_HH_MM_SS);
			Date userDate = DateUtil.stringToDate(dateString, Constants.YYYY_MM_DD_HH_MM_SS);
			userEntity.setCreationDate(userDate);
			String encryptpassword = PasswordCrypt.encrypt(PasswordCrypt.decryptPasswordUI(userEntity.getPassword()));
			userEntity.setPassword(encryptpassword);
			String encryptVpnPassword = PasswordCrypt
					.encrypt(PasswordCrypt.decryptPasswordUI(userEntity.getVpnPassword()));
			userEntity.setPassword(encryptpassword);
			userEntity.setVpnPassword(encryptVpnPassword);
			status = objUserDetailsRepository.createUser(userEntity);
		} catch (Exception e) {
			logger.error("Exception UserDetailsServiceImpl.createUser(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * this method will return UserList
	 * 
	 * @return List<UserDetailsModel>
	 */
	@Override
	public Map<String, Object> getUserList(User user, UserDetailsModel userDetailsModel, Integer customerId, int page, int count) {
		List<UserDetailsModel> userListModelList = null;
		Map<String, Object> resultMap = null;
		try {
			resultMap = objUserDetailsRepository.getUserList(user, userDetailsModel, customerId, page, count);
			List<UserDetailsModel> userList = (List<UserDetailsModel>) resultMap.get("userList");
			if (userList != null && userList.size() > 0) {
				userListModelList = new ArrayList<>();
				for (UserDetailsModel objUserDetailsModel : userList) {
					String encryptpassword = PasswordCrypt.decrypt(objUserDetailsModel.getPassword());
					String encryptVpnPassword = PasswordCrypt.decrypt(objUserDetailsModel.getVpnPassword());
					objUserDetailsModel.setPassword(encryptpassword);
					objUserDetailsModel.setCnfrmPswd(encryptpassword);
					objUserDetailsModel.setVpnPassword(encryptVpnPassword);
					if (StringUtils.isNotEmpty(objUserDetailsModel.getProgramNamehidden())) {
						objUserDetailsModel.setProgramName(objUserDetailsModel.getProgramNamehidden().split(","));
					}
					userListModelList.add(objUserDetailsModel);
				}
			}
			resultMap.put("userList", userListModelList);
		} catch (Exception e) {
			logger.error("Exception UserDetailsServiceImpl.getUserList(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}

	/**
	 * this method will delete the User Details.
	 * 
	 * @param userId
	 * @return boolean
	 */
	@Override
	public boolean deleteUser(int userId) {
		boolean status = false;
		try {
			status = objUserDetailsRepository.deleteUser(userId);
		} catch (Exception e) {
			logger.error("Exception UserDetailsServiceImpl.deleteUser(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * this method will update user details
	 * 
	 * @param userEntity
	 * @return boolean
	 */
	@Override
	public boolean updateUser(UserDetailsEntity userEntity) {
		boolean status = false;
		try {
			String encryptpassword = PasswordCrypt.encrypt(PasswordCrypt.decryptPasswordUI(userEntity.getPassword()));
			String encryptVpnPassword = PasswordCrypt
					.encrypt(PasswordCrypt.decryptPasswordUI(userEntity.getVpnPassword()));
			userEntity.setPassword(encryptpassword);
			userEntity.setVpnPassword(encryptVpnPassword);
			status = objUserDetailsRepository.updateUser(userEntity);
		} catch (Exception e) {
			logger.error("Exception UserDetailsServiceImpl.updateUser(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * 
	 * this method will check duplicate user
	 * 
	 * @param userUserDetailsModel
	 * @return boolean
	 */
	@Override
	public boolean duplicateUser(UserDetailsModel userUserDetailsModel) {
		boolean status = false;
		try {
			status = objUserDetailsRepository.duplicateUser(userUserDetailsModel);
		} catch (Exception e) {
			logger.error("Exception UserDetailsServiceImpl.duplicateUser(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * 
	 * this api will return total size of memory, free memory, used memory,
	 * 
	 * @return DashBoardModel
	 * 
	 */
	@SuppressWarnings("restriction")
	@Override
	public DashBoardModel getDashBoardCountDetails() {
		DashBoardModel objDashBoardModel = new DashBoardModel();
		try {
			long totmemorySize = ((com.sun.management.OperatingSystemMXBean) ManagementFactory
					.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
			long freememorySize = ((com.sun.management.OperatingSystemMXBean) ManagementFactory
					.getOperatingSystemMXBean()).getFreePhysicalMemorySize();
			long usedmemorySize = totmemorySize - freememorySize;
			float usedMemorypercentage = (float) ((Double.valueOf(usedmemorySize) * 100) / totmemorySize);
			float freeMemorypercentage = (float) ((Double.valueOf(freememorySize) * 100) / totmemorySize);

			long diskTotSpace = new File("/").getTotalSpace();
			long diskFreeSpace = new File("/").getFreeSpace();
			long diskUsedSpace = diskTotSpace - diskFreeSpace;
			// ((Double.valueOf(objDashBoardModel.getSysCount())*100)/totalCount);
			float usedDiskpercentage = (float) ((Double.valueOf(diskUsedSpace) * 100 / diskTotSpace));
			float freeDiskpercentage = (float) ((Double.valueOf(diskFreeSpace) * 100 / diskTotSpace));
			// Runtime objRuntime=Runtime.getRuntime();
			objDashBoardModel.setTotalMemory(String.valueOf((totmemorySize / 1E+9) + " GB"));
			objDashBoardModel.setFreeMemory(String.valueOf((freememorySize / 1E+9) + " GB"));
			objDashBoardModel.setUsedMemory(String.valueOf((usedmemorySize / 1E+9) + " GB"));
			objDashBoardModel.setUsedMemoryPercentage(String.valueOf(usedMemorypercentage) + " %");
			objDashBoardModel.setFreeMemoryPercentage(String.valueOf(freeMemorypercentage) + " %");

			objDashBoardModel.setDiskTotalSpace(String.valueOf((diskTotSpace / 1E+9) + " GB"));
			objDashBoardModel.setDiskFreeSpace(String.valueOf((diskFreeSpace / 1E+9) + " GB"));
			objDashBoardModel.setUsedDiskSpace(String.valueOf((diskUsedSpace / 1E+9) + " GB"));
			objDashBoardModel.setUsedDiskSpacePercentage(String.valueOf(usedDiskpercentage) + " %");
			objDashBoardModel.setFreeDiskSpacePercentage(String.valueOf(freeDiskpercentage) + " %");
			ConcurrentHashMap<String, User> map = new ConcurrentHashMap<String, User>();
					map=GlobalStatusMap.loginUsersDetails;
			for(Map.Entry<String, User> entry:map.entrySet()) {
				User user = entry.getValue();
				if (System.currentTimeMillis()
						- user.getLastAccessedTime() > GlobalInitializerListener.MAX_INACTIVE_SESSION_TIMEOUT) {
					GlobalStatusMap.loginUsersDetails.remove(user.getTokenKey());
					UserSessionPool.getInstance().removeUser(user);
				}
			}
			objDashBoardModel.setActiveUsersCount(String.valueOf(GlobalStatusMap.loginUsersDetails.size()));
		} catch (Exception e) {
			logger.error("Exception in UserDetailsServiceImpl.getDashBoardCountDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objDashBoardModel;
	}

	/**
	 * 
	 * this api will getCpuUsage
	 * 
	 * @return StringBuilder
	 * 
	 */
	@Override
	public StringBuilder getCpuUsage() {
		StringBuilder cpuUsage = new StringBuilder();
		try {
			String[] cpuUsageCommand = { "/bin/sh", "-c", Constants.CPU_USAGE_COMMAND };
			Process p = Runtime.getRuntime().exec(cpuUsageCommand);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				cpuUsage.append(line + "\n");
			}
		} catch (IOException e) {
			logger.error("Exception in UserDetailsServiceImpl.getCpuUsage(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return cpuUsage;
	}
	
	/**
	 * 
	 * this api will getMapDetails
	 * 
	 * @return List<getMapDetails>
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getMapDetails(SchedulingSRModel schedulingSRModel, List<CustomerEntity> customerEntities) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String,String>> list = new ArrayList<Map<String, String>>();
		try {
			Map<String, String> neMap = new HashMap<String, String>();
			Map<String, Object> map = schedulingSRRepository.getNeDetailsForMap(schedulingSRModel, customerEntities);
			Map<String,Date> neDetails = (Map<String,Date>) map.get("neList");
			if(CommonUtil.isValidObject(neDetails) && neDetails.size()>0){
				for(Entry<String, Date> neDetail: neDetails.entrySet()){
					String neId = neDetail.getKey();
					String compDate = DateUtil.dateToString(neDetail.getValue(),Constants.DD_MM_YYYY_);
					if(StringUtils.isNotBlank(neId)){
						List<CIQDetailsModel> enbDetails = fileUploadRepository.getEnbDetails(neId);
						if(CommonUtil.isValidObject(enbDetails) && enbDetails.size() > 0){
						String latitude = enbDetails.get(0).getCiqMap().get(Constants.MAP_LATITUDE).getHeaderValue().toString();
						String longitude = enbDetails.get(0).getCiqMap().get(Constants.MAP_LONGITUDE).getHeaderValue().toString();
						String enbName = enbDetails.get(0).geteNBName().toString();
						String market = enbDetails.get(0).getCiqMap().get(Constants.MAP_MARKET).getHeaderValue().toString();
						String cellId = enbDetails.get(0).getCiqMap().get(Constants.MAP_Cell_ID).getHeaderValue().toString();
						
						NeMappingModel neMappingModel = new NeMappingModel();
						CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
						neMappingModel.setProgramDetailsEntity(programDetailsEntity);
						neMappingModel.setEnbId(neId);
						List<NeMappingEntity> neMappingEntities = neMappingService.getNeMapping(neMappingModel);
						String lsmName="";
						String nwtype="";
						if(CommonUtil.isValidObject(neMappingEntities) && neMappingEntities.size()>0){
							NeMappingEntity neMappingEntity = neMappingEntities.get(0);
							if(CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity())){
								lsmName=neMappingEntity.getNetworkConfigEntity().getNeName();
							}
							nwtype=neMappingEntity.getProgramDetailsEntity().getNetworkTypeDetailsEntity().getNetworkType();
						}
						
						String information = Constants.MAP_ENB_NAME.toUpperCase()+": "+enbName+"<br/>";
						information = information + Constants.MAP_Cell_ID.replace("_", " ").toUpperCase()+": "+cellId+"<br/>";
						information = information + Constants.MAP_NW_TYPE.toUpperCase()+": "+nwtype+"<br/>";
						information = information + Constants.MAP_MARKET.toUpperCase()+": "+market+"<br/>";
						information = information + Constants.MAP_LATITUDE.toUpperCase()+": "+latitude+"<br/>";
						information = information + Constants.MAP_LONGITUDE.toUpperCase()+": "+longitude+"<br/>";
						information = information + Constants.MAP_COMM_COMP_DATE.toUpperCase()+": "+compDate;
						
						neMap = new HashMap<String, String>();
						neMap.put("latitude", latitude); 
						neMap.put("longitude",longitude); 
						neMap.put("information", information);
						list.add(neMap);
						}
					}
				}
			}
			resultMap.put("market", map.get("market"));
			resultMap.put("customerList",  map.get("customerList"));
			resultMap.put("list", list);
			logger.info("UserDetailsServiceImpl.getMapDetails() markers found with all info: " +list.size());	
		}catch(Exception e) {
			logger.error("Exception in UserDetailsServiceImpl.getMapDetails(): " + ExceptionUtils.getFullStackTrace(e));	
		}
		return resultMap;	
	}

	/**
	 * 
	 * this api will getPageCount
	 * 
	 * @param page,count
	 * 
	 * @return Object
	 * 
	 */
	@Override
	public Object getPageCount(int page, int count) {
		Object paging = null;
		try {
			paging = objUserDetailsRepository.getPageCount(page, count);
		} catch (Exception e) {
			logger.error("Exception in UserDetailsServiceImpl.getUserList(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return paging;
	}

	/**
	 * 
	 * this api will getUserById
	 * 
	 * @param articleId
	 * 
	 * @return UserDetailsEntity
	 * 
	 */
	@Override
	public UserDetailsEntity getUserById(int articleId) {
		UserDetailsEntity detailsEntity = null;
		try {
			detailsEntity = objUserDetailsRepository.getUserById(articleId);
		} catch (Exception e) {
			logger.error("Exception in UserDetailsServiceImpl.getUserById(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return detailsEntity;
	}
	
	@Override
	public List<String> getUserNameList(User user, UserDetailsModel userDetailsModel, Integer customerId) {
		List<UserDetailsModel> userListModelList = null;
		Map<String, Object> resultMap = null;
		List<String> userNameList = new ArrayList();
		try {
			resultMap = objUserDetailsRepository.getUserNameList(user, userDetailsModel, customerId);
			List<UserDetailsModel> userList = (List<UserDetailsModel>) resultMap.get("userList");
			if (userList != null && userList.size() > 0) {
				userListModelList = new ArrayList<>();
				for (UserDetailsModel objUserDetailsModel : userList) {
					userNameList.add(objUserDetailsModel.getUserName());
				}
			}
			resultMap.put("userList", userListModelList);
		} catch (Exception e) {
			logger.error("Exception UserDetailsServiceImpl.getUserList(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return userNameList;
	}
}
