package com.smart.rct.premigration.serviceImpl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.rct.premigration.entity.CiqUploadAuditTrailDetEntity;
import com.smart.rct.premigration.models.EnbPreGrowAuditModel;
import com.smart.rct.premigration.repository.EnbPreGrowRepository;
import com.smart.rct.premigration.service.EnbPreGrowService;
import com.smart.rct.usermanagement.models.User;

@Service
public class EnbPreGrowServiceImpl implements EnbPreGrowService{
	final static Logger logger = LoggerFactory.getLogger(EnbPreGrowServiceImpl.class);
	
	@Autowired
	EnbPreGrowRepository enbPreGrowRepository;

	
	/**
	 * This api is to get the NeGrowDetails
	 * 
	 * @param enbModel,page,count
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getNeGrowDetails(EnbPreGrowAuditModel enbModel, int page, int count) {
		// TODO Auto-generated method stub
		Map<String, Object> objMap =null;
		try {
			objMap = enbPreGrowRepository.getNeGrowDetails(enbModel,page,count);
			objMap.put("useCaseList", enbPreGrowRepository.getUseCaseList(null));

		} catch (Exception e) {
			// TODO: handle exception

			logger.error("getCsvAuditDetails() GenerateCsvServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		}
		return objMap;
	}

	/**
	 * This api is to get the CIQ List from Retrieve CIQ table
	 * 
	 * @param user,programId,fromDate,toDate
	 * @return List<CiqUploadAuditTrailDetEntity>
	 */
	@Override
	public List<CiqUploadAuditTrailDetEntity> getCiqList(User user, String programId, String fromDate, String toDate) {
		List<CiqUploadAuditTrailDetEntity> ciqList = null;
		try {
			ciqList = enbPreGrowRepository.getCiqList(user, programId, fromDate, toDate);
		} catch (Exception e) {
			logger.error("Exception getCiqList() in UserDetailsRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		}
		return ciqList;
	}

	/**
	 * This api is to get the SM version and SM Names
	 * 
	 * @param programId
	 * @return Map<String, List<String>>
	 */
	@Override
	public  Map<String, List<String>> getSmDetails(Integer programId) {
		 Map<String, List<String>> smList = null;
		try {
			smList = enbPreGrowRepository.getSmDetails(programId);

		} catch (Exception e) {
			logger.error(" getSmList service : " + ExceptionUtils.getFullStackTrace(e));
		}

		return smList;
	}

	
	/**
	 * This api is to get the SM version and SM Names for search
	 * 
	 * @param programId
	 * @return Map<String, List<String>>
	 */
	@Override
	public Map<String, List<String>> getSmSearchDetails(Integer programId) {
		 Map<String, List<String>> smList = null;
			try {
				smList = enbPreGrowRepository.getSmSearchDetails(programId);

			} catch (Exception e) {
				logger.error(" getSmList service : " + ExceptionUtils.getFullStackTrace(e));
			}

			return smList;
		}

	
	/**
	 * This api is to get the ciq and Ne Names
	 * 
	 * @param programId
	 * @return Map<String, List<String>>
	 */
	@Override
	public Map<String, List<String>> getCiqNeSearchDetails(Integer programId) {
		 Map<String, List<String>> smList = null;
			try {
				smList = enbPreGrowRepository.getCiqNeSearchDetails(programId);

			} catch (Exception e) {
				logger.error(" getSmList service : " + ExceptionUtils.getFullStackTrace(e));
			}

			return smList;
	}

	
}
