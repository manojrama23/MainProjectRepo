package com.smart.rct.common.repositoryImpl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.CustomerEntity;
import com.smart.rct.common.entity.DuoGeneralConfigEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.OvGeneralEntity;
import com.smart.rct.common.entity.ProgramGenerateFileEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.common.entity.SchedulingReportsTemplateEntity;
import com.smart.rct.common.entity.SnrGeneralEntity;
import com.smart.rct.common.models.CustomerDetailsModel;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.common.repository.CustomerRepository;
import com.smart.rct.common.service.NetworkConfigService;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.exception.RctException;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.migration.entity.UseCaseBuilderEntity;
import com.smart.rct.migration.entity.UseCaseBuilderParamEntity;
import com.smart.rct.migration.entity.XmlRuleBuilderEntity;
import com.smart.rct.migration.service.UseCaseBuilderService;
import com.smart.rct.migration.service.XmlRuleBuilderService;
import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.LoadPropertyFiles;

@Repository
@EnableTransactionManagement
@Transactional
public class CustomerRepositoryImpl implements CustomerRepository {

	final static Logger logger = LoggerFactory.getLogger(CustomerRepositoryImpl.class);

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	UseCaseBuilderService useCaseBuilderService;
	
	@Autowired
	XmlRuleBuilderService xmlRuleBuilderService;
	
	@Autowired
	NetworkConfigService networkConfigService;
	
	/**
	 * This api get the CustomerList
	 * 
	 * @param addAllRecord
	 * @return List<CustomerEntity>
	 */
	@Override
	public List<CustomerEntity> getCustomerList(boolean addAllRecord, boolean addInActiveCustomers) {
		List<CustomerEntity> customerEntityList = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<CustomerEntity> query = builder.createQuery(CustomerEntity.class);
			Root<CustomerEntity> root = query.from(CustomerEntity.class);
			query.select(root);
			if (!addAllRecord && !addInActiveCustomers) {
				query.where(builder.and(builder.notEqual(root.get("id"), 1), builder.equal(root.get("status"), Constants.ACTIVE)));
			} else if (!addAllRecord && addInActiveCustomers) {
				query.where(builder.and(builder.notEqual(root.get("id"), 1)));
			}else if (addAllRecord && !addInActiveCustomers) {
				query.where(builder.equal(root.get("status"), Constants.ACTIVE));
			} else if (addAllRecord && addInActiveCustomers) {
				//Nothing to do
			}
			TypedQuery<CustomerEntity> queryResult = entityManager.createQuery(query);
			customerEntityList = queryResult.getResultList();
			if (CommonUtil.isValidObject(customerEntityList)) {
				if (!addInActiveCustomers) {
					customerEntityList = customerEntityList.stream()
							.peek(x -> x.setCustomerDetails(x.getCustomerDetails().parallelStream()
									.filter(y -> y.getStatus().equalsIgnoreCase(Constants.ACTIVE))
									.collect(Collectors.toList())))
							.collect(Collectors.toList());
				}
				customerEntityList.stream()
						.peek(x -> x.getCustomerDetails()
								.sort((p1, p2) -> p1.getProgramName().compareToIgnoreCase(p2.getProgramName())))
						.collect(Collectors.toList());
			}
		} catch (Exception e) {
			logger.info("Exception in CustomerRepositoryImpl.getCustomerList(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return customerEntityList;
	}
	

	/**
	 * This api get the CustomerDetailsList by customerId
	 * 
	 * @param customerId
	 * @return CustomerEntity
	 */
	@Override
	public List<CustomerDetailsEntity> getCustomerDetails(Integer customerId) {
		List<CustomerDetailsEntity> customerDetailsEntities = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<CustomerDetailsEntity> query = builder.createQuery(CustomerDetailsEntity.class);
			Root<CustomerDetailsEntity> root = query.from(CustomerDetailsEntity.class);
			query.select(root);
			query.where(builder.equal(root.get("customerId"), customerId));
			query.orderBy(builder.asc(root.get("programName")));
			TypedQuery<CustomerDetailsEntity> queryResult = entityManager.createQuery(query);
			customerDetailsEntities = queryResult.getResultList();
		} catch (Exception e) {
			logger.info(
					"Exception in CustomerRepositoryImpl.getCustomerDetails(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return customerDetailsEntities;
	}

	/**
	 * This api get the CustomerList by customerId
	 * 
	 * @param customerId
	 * @return CustomerEntity
	 */
	@Override
	public CustomerEntity getCustomerById(Integer customerId) {
		logger.info("CustomerRepositoryImpl.getCustomerById() customerId: " + customerId);
		CustomerEntity customerEntity = null;
		try {
			customerEntity =  entityManager.find(CustomerEntity.class, customerId);
		} catch (Exception e) {
			logger.info("Exception in CustomerRepositoryImpl.getCustomerById(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return customerEntity;
	}

	/**
	 * This api saves customer and its details
	 * 
	 * @param customerEntity
	 * @return String
	 */
	@Override
	public CustomerEntity saveCustomer(CustomerEntity customerEntity) throws RctException {
		boolean exceptionStatus = false;
		try {
			customerEntity = entityManager.merge(customerEntity);
		} catch (Exception e) {
			if (e.getCause() instanceof ConstraintViolationException) {
				exceptionStatus = true;
				throw new RctException(
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.PROGRAM_NAME_ALREADY_USED));
			}
			logger.error("Exception in CustomerRepositoryImpl.saveCustomer(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			if (!exceptionStatus) {
				entityManager.flush();
			}
			entityManager.clear();
		}
		return customerEntity;
	}

	/**
	 * This api delete the customer and its details
	 * 
	 * @param customerId
	 * @return boolean
	 */
	@Override
	public boolean deleteCustomer(Integer customerId) {
		boolean status = false;
		try {
			logger.info("CustomerRepositoryImpl.deleteCustomer() customerId: "+customerId);
			
			//S&R
			entityManager.createQuery("DELETE FROM  SchedulingReportsTemplateEntity WHERE customerEntity.id="+customerId).executeUpdate();
			//User
			entityManager.createQuery("DELETE FROM  UserDetailsEntity WHERE customerEntity.id="+customerId).executeUpdate();
			
			entityManager.remove(entityManager.find(CustomerEntity.class, customerId));
			/*CustomerEntity customerEntity = getCustomerById(customerId);
			customerEntity.setStatus(Constants.INACTIVE);

			if (CommonUtil.isValidObject(customerEntity.getCustomerDetails())
					&& customerEntity.getCustomerDetails().size() > 0) {
				customerEntity.getCustomerDetails().forEach((u) -> u.setStatus(Constants.INACTIVE));
			}
			entityManager.merge(customerEntity);*/
			status = true;

		} catch (Exception e) {
			status = false;
			logger.info("Exception in CustomerRepositoryImpl.deleteCustomer(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	
	/**
	 * This api delete the customer and its details
	 * 
	 * @param customerId
	 * @return boolean
	 */
	@Override
	public boolean inActivateCustomer(Integer customerId) {
		boolean status = false;
		try {
			CustomerEntity customerEntity = getCustomerById(customerId);
			customerEntity.setStatus(Constants.INACTIVE);

			if (CommonUtil.isValidObject(customerEntity.getCustomerDetails()) && customerEntity.getCustomerDetails().size() > 0) {
				customerEntity.getCustomerDetails().forEach((u) -> u.setStatus(Constants.INACTIVE));
			}
			entityManager.merge(customerEntity);
			status = true;

		} catch (Exception e) {
			status = false;
			logger.info("Exception in CustomerRepositoryImpl.deleteCustomer(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This api delete the customer details by id
	 * 
	 * @param customerDetailId
	 * @return boolean
	 * @throws RctException
	 */
	@Override
	public boolean deleteCustomerDetails(Integer customerDetailId) throws RctException {
		logger.info("CustomerRepositoryImpl deleteCustomerDetails(): " + customerDetailId);
		boolean status = false;
		try {
			//Migration
			entityManager.createQuery("DELETE FROM  RunTestResultEntity WHERE customerDetailsEntity.id="+customerDetailId).executeUpdate();
			entityManager.createQuery("DELETE FROM  RunTestEntity WHERE customerDetailsEntity.id="+customerDetailId).executeUpdate();
			
			List<UseCaseBuilderEntity> useCaseBuilderEntityList = useCaseBuilderService.getUseCaseBuilderEntityList(customerDetailId);
			if(CommonUtil.isValidObject(useCaseBuilderEntityList) && useCaseBuilderEntityList.size() > 0){
				for(UseCaseBuilderEntity useCaseBuilderEntity: useCaseBuilderEntityList){
					if(CommonUtil.isValidObject(useCaseBuilderEntity.getUseCaseBuilderParamEntity()) && useCaseBuilderEntity.getUseCaseBuilderParamEntity().size() > 0){
						for(UseCaseBuilderParamEntity useCaseBuilderParamEntity: useCaseBuilderEntity.getUseCaseBuilderParamEntity()){
							entityManager.createQuery("DELETE FROM  UseCaseCmdRuleEntity WHERE useCaseBuilderParamEntity.id="+useCaseBuilderParamEntity.getId()).executeUpdate();
							entityManager.createQuery("DELETE FROM  UseCaseFileRuleEntity WHERE useCaseBuilderScriptsEntity.id="+useCaseBuilderParamEntity.getId()).executeUpdate();
							entityManager.createQuery("DELETE FROM  UseCaseShellRuleEntity WHERE useCaseBuilderParamEntity.id="+useCaseBuilderParamEntity.getId()).executeUpdate();
							entityManager.createQuery("DELETE FROM  UseCaseXmlRuleEntity WHERE useCaseBuilderScriptsEntity.id="+useCaseBuilderParamEntity.getId()).executeUpdate();
							entityManager.createQuery("DELETE FROM  UseCaseBuilderParamEntity WHERE id="+useCaseBuilderParamEntity.getId()).executeUpdate();
						}
						entityManager.createQuery("DELETE FROM  UseCaseBuilderEntity WHERE id="+useCaseBuilderEntity.getId()).executeUpdate();
					}
				}
			}
			
			entityManager.createQuery("DELETE FROM  UseCaseBuilderParamEntity WHERE customerDetailsEntity.id="+customerDetailId).executeUpdate();
			entityManager.createQuery("DELETE FROM  UseCaseBuilderEntity WHERE customerDetailsEntity.id="+customerDetailId).executeUpdate();
			
			entityManager.createQuery("DELETE FROM  UploadFileEntity WHERE customerDetailsEntity.id="+customerDetailId).executeUpdate();
			entityManager.createQuery("DELETE FROM  CmdRuleBuilderEntity WHERE customerDetailsEntity.id="+customerDetailId).executeUpdate();
			entityManager.createQuery("DELETE FROM  FileRuleBuilderEntity WHERE customerDetailsEntity.id="+customerDetailId).executeUpdate();
			entityManager.createQuery("DELETE FROM  ShellCmdRuleBuilderEntity WHERE customerDetailsEntity.id="+customerDetailId).executeUpdate();
			
			List<XmlRuleBuilderEntity> xmlRuleBuilderEntityList = xmlRuleBuilderService.getXmlRuleBuilderEntityList(customerDetailId);
			if(CommonUtil.isValidObject(xmlRuleBuilderEntityList) && xmlRuleBuilderEntityList.size() > 0){
				for(XmlRuleBuilderEntity xmlRuleBuilderEntity: xmlRuleBuilderEntityList){
					entityManager.createQuery("DELETE FROM  XmlRootEntity WHERE xmlRuleBuilderEntity.id="+xmlRuleBuilderEntity.getId()).executeUpdate();
					entityManager.createQuery("DELETE FROM  XmlElementEntity WHERE xmlRuleBuilderEntity.id="+xmlRuleBuilderEntity.getId()).executeUpdate();
				}
			}
			
			entityManager.createQuery("DELETE FROM  XmlRuleBuilderEntity WHERE customerDetailsEntity.id="+customerDetailId).executeUpdate();
			
			//Pre Migration
			entityManager.createQuery("DELETE FROM  CheckListScriptDetEntity WHERE programDetailsEntity.id="+customerDetailId).executeUpdate();
			entityManager.createQuery("DELETE FROM  CiqUploadAuditTrailDetEntity WHERE programDetailsEntity.id="+customerDetailId).executeUpdate();
			//entityManager.createQuery("DELETE FROM  CiqValidationRulesEntity WHERE programDetailsEntity.id="+customerDetailId).executeUpdate();
			entityManager.createQuery("DELETE FROM  EnbPreGrowAuditEntity WHERE programDetailsEntity.id="+customerDetailId).executeUpdate();
			entityManager.createQuery("DELETE FROM  GenerateInfoAuditEntity WHERE programDetailsEntity.id="+customerDetailId).executeUpdate();
			entityManager.createQuery("DELETE FROM  NeMappingEntity WHERE programDetailsEntity.id="+customerDetailId).executeUpdate();
			entityManager.createQuery("DELETE FROM  NeConfigTypeEntity WHERE programDetailsEntity.id="+customerDetailId).executeUpdate();
			
			//Post Migration
			entityManager.createQuery("DELETE FROM  SiteDataEntity WHERE programDetailsEntity.id="+customerDetailId).executeUpdate();
			
			
			//Common
			entityManager.createQuery("DELETE FROM  GrowConstantsEntity WHERE programDetailsEntity.id="+customerDetailId).executeUpdate();
			
			List<NetworkConfigEntity> networkConfigEntityList = networkConfigService.getNetworkConfigList(customerDetailId);
			if(CommonUtil.isValidObject(networkConfigEntityList) && networkConfigEntityList.size() > 0){
				for(NetworkConfigEntity networkConfigEntity: networkConfigEntityList){
					entityManager.createQuery("DELETE FROM  NetworkConfigDetailsEntity WHERE networkConfigEntity.id="+networkConfigEntity.getId()).executeUpdate();
				}
			}
			
			entityManager.createQuery("DELETE FROM  NetworkConfigEntity WHERE programDetailsEntity.id="+customerDetailId).executeUpdate();
			entityManager.createQuery("DELETE FROM  NeVersionEntity WHERE programDetailsEntity.id="+customerDetailId).executeUpdate();
			entityManager.createQuery("DELETE FROM  ProgramGenerateFileEntity WHERE programDetailsEntity.id="+customerDetailId).executeUpdate();
			entityManager.createQuery("DELETE FROM  ProgramTemplateEntity WHERE programDetailsEntity.id="+customerDetailId).executeUpdate();
			
			//Actual Program Delete 
			entityManager.createQuery("DELETE FROM  CustomerDetailsEntity WHERE id="+customerDetailId).executeUpdate();
			
			status = true;
			
		} catch (Exception e) {
			if (e.getCause() instanceof ConstraintViolationException) {
				throw new RctException(GlobalInitializerListener.faultCodeMap.get(FaultCodes.CUSTOMER_DETAILS_ASSOSIATED));
			}
			status = false;
			logger.error("Exception in CustomerRepositoryImpl.deleteCustomerDetails(): "+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			if(status){
				StringBuilder programFolderPath = new StringBuilder();
				programFolderPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
				.append(Constants.CUSTOMER)
				.append(Constants.SEPARATOR).append(customerDetailId);
				File dir = new File(programFolderPath.toString());
				if (dir.exists()) {
					try {
						FileUtils.deleteDirectory(dir);
					} catch (IOException e) {
						logger.error("Exception in CustomerRepositoryImpl.deleteCustomerDetails(): "+ ExceptionUtils.getFullStackTrace(e));
						e.printStackTrace();
					}
				}
			}
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This method will deleteProgramTemplate
	 * 
	 * @param programTemplateId
	 * @return boolean
	 */
	@Override
	public boolean deleteProgramTemplate(Integer programTemplateId) {
		logger.info("CustomerRepositoryImpl deleteProgramTemplate(): " + programTemplateId);
		boolean status = false;
		try {
			Query query = entityManager.createQuery("DELETE FROM  ProgramTemplateEntity WHERE id=:id");
			query.setParameter("id", programTemplateId);
			query.executeUpdate();
			status = true;
		} catch (Exception e) {
			status = false;
			logger.info("Exception in CustomerRepositoryImpl.deleteProgramTemplate(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This method will deleteProgramGenerateFileEntity
	 * 
	 * @param programTemplateId
	 * @return boolean
	 */
	@Override
	public boolean deleteProgramGenerateFileEntity(Integer programTemplateId) {
		logger.info("CustomerRepositoryImpl deleteProgramGenerateFileEntity(): " + programTemplateId);
		boolean status = false;
		try {
			Query query = entityManager.createQuery("DELETE FROM  ProgramGenerateFileEntity WHERE id=:id");
			query.setParameter("id", programTemplateId);
			query.executeUpdate();
			status = true;
		} catch (Exception e) {
			status = false;
			logger.info("Exception in CustomerRepositoryImpl.deleteProgramGenerateFileEntity(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This method will getCustomerByName
	 * 
	 * @param customerName
	 * @return CustomerEntity
	 */
	@Override
	public CustomerEntity getCustomerByName(String customerName) {
		logger.info("CustomerRepositoryImpl.getCustomerByName() customerName: " + customerName);
		CustomerEntity customerEntity = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<CustomerEntity> query = cb.createQuery(CustomerEntity.class);
			Root<CustomerEntity> root = query.from(CustomerEntity.class);
			query.select(root).where(cb.equal(root.get("customerName"), customerName));
			TypedQuery<CustomerEntity> typedQuery = entityManager.createQuery(query);
			customerEntity = typedQuery.getResultList().stream().findFirst().orElse(null);
			;
		} catch (Exception e) {
			logger.info(
					"Exception in CustomerRepositoryImpl.getCustomerByName(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return customerEntity;
	}

	/**
	 * This method will saveProgramTemplate
	 * 
	 * @param objProgramTemplateEntity
	 * @return boolean
	 */
	@Override
	public boolean saveProgramTemplate(ProgramTemplateEntity objProgramTemplateEntity) {
		logger.info("CustomerRepositoryImpl.saveProgramTemplate()");
		boolean status = false;
		try {
			entityManager.merge(objProgramTemplateEntity);
			status = true;
		} catch (Exception e) {
			logger.info("Exception in CustomerRepositoryImpl.saveProgramTemplate(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	
	/**
	 * This method will saveOvTemplate
	 * 
	 * @param objProgramTemplateEntity
	 * @return boolean
	 */
	@Override
	public boolean saveOvTemplate(OvGeneralEntity objOvGeneralEntity) {
		logger.info("CustomerRepositoryImpl.saveOvTemplate()");
		boolean status = false;
		try {
			entityManager.merge(objOvGeneralEntity);
			status = true;
		} catch (Exception e) {
			logger.info("Exception in CustomerRepositoryImpl.saveOvTemplate(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	@Override
	public boolean saveSnrTemplate(SnrGeneralEntity objSnrGeneralEntity) {
		logger.info("CustomerRepositoryImpl.saveOvTemplate()");
		boolean status = false;
		try {
			entityManager.merge(objSnrGeneralEntity);
			status = true;
		} catch (Exception e) {
			logger.info("Exception in CustomerRepositoryImpl.saveOvTemplate(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This method will duplicateProgaramTemplate
	 * 
	 * @param programTemplateModel
	 * @return boolean
	 */
	@SuppressWarnings("deprecation")
	@Override
	public boolean duplicateProgaramTemplate(ProgramTemplateModel programTemplateModel) {
		logger.info("CustomerRepositoryImpl.duplicateProgaramTemplate()");
		boolean status = false;
		try {
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(ProgramTemplateEntity.class);
			criteria.createAlias("programDetailsEntity", "programDetailsEntity");
			criteria.add(
					Restrictions.eq("programDetailsEntity.id", programTemplateModel.getProgramDetailsEntity().getId()));
			criteria.add(Restrictions.eq("label", programTemplateModel.getLabel()));
			criteria.setProjection(Projections.rowCount());
			Long duplicatecount = (Long) criteria.uniqueResult();
			if (duplicatecount.intValue() > 0) {
				status = true;
			}
		} catch (Exception e) {
			logger.info("Exception in CustomerRepositoryImpl.duplicateProgaramTemplate(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This method will duplicateProgaramTemplate
	 * 
	 * @return List<ProgramTemplateEntity>
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<ProgramTemplateEntity> getProgramTemplate(String configType) {
		List<ProgramTemplateEntity> objProgramTemplateEntityList = null;
		try {
			Criteria criteriaTotList = entityManager.unwrap(Session.class).createCriteria(ProgramTemplateEntity.class);
			if(StringUtils.isNotEmpty(configType)){
				criteriaTotList.add(Restrictions.eq("configType", configType));
			}
			objProgramTemplateEntityList = criteriaTotList.list();
		} catch (Exception e) {
			logger.info(
					"Exception in CustomerRepositoryImpl.getProgramTemplate(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objProgramTemplateEntityList;
	}
	
	
	/**
	 * This method will give keyValues
	 * 
	 * @return objOvGeneralEntity
	 */
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public ProgramTemplateEntity getKeyValuesTemplate(ProgramTemplateModel programTemplateModel, String label) {
		ProgramTemplateEntity objOvGeneralEntity = null;
		
		try {
			
			Criteria criteriaTotList = entityManager.unwrap(Session.class).createCriteria(ProgramTemplateEntity.class);
			criteriaTotList.createAlias("programDetailsEntity", "programDetailsEntity");
			if(StringUtils.isNotEmpty(label)){
				
				criteriaTotList.add(Restrictions.eq("label", label));
				
			}
			criteriaTotList.add(
					Restrictions.eq("programDetailsEntity.programName",programTemplateModel.getProgramDetailsEntity().getProgramName()));
			
			List<ProgramTemplateEntity> objOvTemplateEntityList = criteriaTotList.list();
			
			for(  ProgramTemplateEntity objListOv : objOvTemplateEntityList ) {
			if(!ObjectUtils.isEmpty(objOvTemplateEntityList)&& objListOv.getProgramDetailsEntity().getProgramName()!=null ){
				objOvGeneralEntity=objOvTemplateEntityList.get(0);
			
		}} }catch (Exception e) {
			logger.info(
					"Exception in CustomerRepositoryImpl.getOvTemplate(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objOvGeneralEntity;
	}
	
	/**
	 * This method will return milestones
	 * 
	 * @return objOvGeneralEntity
	 */
	
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public ProgramTemplateEntity getMileStonesTemplate(ProgramTemplateModel programTemplateModel, String label) {
		ProgramTemplateEntity objOvGeneralEntity = null;
		
		try {
			
			Criteria criteriaTotList = entityManager.unwrap(Session.class).createCriteria(ProgramTemplateEntity.class);
			criteriaTotList.createAlias("programDetailsEntity", "programDetailsEntity");
			if(StringUtils.isNotEmpty(label)){
				
				criteriaTotList.add(Restrictions.eq("label", label));
				
			}
			criteriaTotList.add(
					Restrictions.eq("programDetailsEntity.programName",programTemplateModel.getProgramDetailsEntity().getProgramName()));
			
			List<ProgramTemplateEntity> objOvTemplateEntityList = criteriaTotList.list();
			
			for(  ProgramTemplateEntity objListOv : objOvTemplateEntityList ) {
			if(!ObjectUtils.isEmpty(objOvTemplateEntityList)&& objListOv.getProgramDetailsEntity().getProgramName()!=null ){
				objOvGeneralEntity=objOvTemplateEntityList.get(0);
			
		}} }catch (Exception e) {
			logger.info(
					"Exception in CustomerRepositoryImpl.getOvTemplate(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objOvGeneralEntity;
	}
	
	
	/**
	 * This method will duplicateProgaramTemplate
	 * 
	 * @return List<OvGeneralEntity>
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<OvGeneralEntity> getOvTemplate(String configType) {
		List<OvGeneralEntity> objOvTemplateEntityList = null;
		try {
			Criteria criteriaTotList = entityManager.unwrap(Session.class).createCriteria(OvGeneralEntity.class);
			if(StringUtils.isNotEmpty(configType)){
				criteriaTotList.add(Restrictions.eq("configType", configType));
			}
			objOvTemplateEntityList = criteriaTotList.list();
		} catch (Exception e) {
			logger.info(
					"Exception in CustomerRepositoryImpl.getOvTemplate(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objOvTemplateEntityList;
	}

	/**
	 * This method will getProgramList
	 * 
	 * @param customerId
	 * @return List<String>
	 */
	@Override
	public List<String> getProgramList(Integer customerId) {
		logger.info("CustomerRepositoryImpl.getProgramList() called.. customerId: " + customerId);
		List<String> programList = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<String> query = builder.createQuery(String.class);
			Root<CustomerDetailsEntity> root = query.from(CustomerDetailsEntity.class);
			query.select(root.<String>get("programName"));
			if (CommonUtil.isValidObject(customerId) && customerId > 1) {
				query.where(builder.equal(root.get("customerId"), customerId), builder.equal(root.get("status"), Constants.ACTIVE));
			}else{
				query.where(builder.equal(root.get("status"), Constants.ACTIVE));
			}
			query.orderBy(builder.asc(root.get("programName")));
			TypedQuery<String> typedQuery = entityManager.createQuery(query);
			programList = (List<String>) typedQuery.getResultList();

		} catch (Exception e) {
			logger.info("Exception in CustomerRepositoryImpl.getProgramList(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return programList;
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<CustomerDetailsEntity> getAllProgramList(CustomerDetailsModel customerDetailsModel) {
		List<CustomerDetailsEntity> customerDetailsEntities = null;
		try {
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(CustomerDetailsEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			criteria.createAlias("customerEntity", "customerEntity");
			if (CommonUtil.isValidObject(customerDetailsModel) && CommonUtil.isValidObject(customerDetailsModel.getCustomerEntity()) && CommonUtil.isValidObject(customerDetailsModel.getCustomerEntity().getId()) && customerDetailsModel.getCustomerEntity().getId() > 1) {
				Criterion customerEntity = Restrictions.eq("customerEntity.id",customerDetailsModel.getCustomerEntity().getId());
				conjunction.add(customerEntity);
			}
			criteria.add(conjunction);
			criteria.addOrder(Order.asc("programName"));
			logger.info("CustomerRepositoryImpl.getAllProgramList() criteria: "+criteria.toString());
			customerDetailsEntities = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.info("Exception in CustomerRepositoryImpl.getAllProgramList(): "+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return customerDetailsEntities;
	}
	/**
	 * This method will getCustomerDetailsList
	 * 
	 * @param customerDetailsModel
	 * @return List<CustomerDetailsEntity>
	 */
	@Override
	public List<CustomerDetailsEntity> getCustomerDetailsList(CustomerDetailsModel customerDetailsModel) {
		List<CustomerDetailsEntity> customerDetailsEntities = null;
		try {
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(CustomerDetailsEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			criteria.createAlias("customerEntity", "customerEntity");
			
			if (CommonUtil.isValidObject(customerDetailsModel) && CommonUtil.isValidObject(customerDetailsModel.getId())) {
				Criterion id = Restrictions.eq("id", customerDetailsModel.getId());
				conjunction.add(id);
			}
			if (CommonUtil.isValidObject(customerDetailsModel) && CommonUtil.isValidObject(customerDetailsModel.getProgramName()) && StringUtils.isNotEmpty(customerDetailsModel.getProgramName())) {
				Criterion programName = Restrictions.ilike("programName", customerDetailsModel.getProgramName());
				conjunction.add(programName);
			}
			if (CommonUtil.isValidObject(customerDetailsModel.getCustomerEntity()) && CommonUtil.isValidObject(customerDetailsModel.getCustomerEntity().getId()) && customerDetailsModel.getCustomerEntity().getId() > 1) {
				Criterion customerEntity = Restrictions.eq("customerEntity.id",customerDetailsModel.getCustomerEntity().getId());
				conjunction.add(customerEntity);
			}
			Criterion status = Restrictions.eq("status", Constants.ACTIVE);
			conjunction.add(status);
			
			Criterion parentStatus = Restrictions.eq("customerEntity.status",Constants.ACTIVE);
			conjunction.add(parentStatus);
			
			criteria.add(conjunction);
			criteria.addOrder(Order.asc("programName"));
			logger.info("CustomerRepositoryImpl.getCustomerDetailsList() criteria: "+criteria.toString());
			customerDetailsEntities = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.info("Exception in CustomerRepositoryImpl.getCustomerDetailsList(): "+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return customerDetailsEntities;
	}
	/**
	 * This method will getProgramDetailsList
	 * 
	 * @param user
	 * @return List<CustomerDetailsEntity>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<CustomerDetailsEntity> getProgramDetailsList(User user) {
		logger.info("CustomerRepositoryImpl.getProgramDetailsList() called ProgramName: " + user.getProgramName());
		List<CustomerDetailsEntity> customerDetailsEntities = null;
		try {
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(CustomerDetailsEntity.class);
			criteria.createAlias("customerEntity", "customerEntity");
			if (CommonUtil.isValidObject(user) && CommonUtil.isValidObject(user.getProgramName())) {
				criteria.add(Restrictions.in("programName", user.getProgramName()));
			}
			criteria.add(Restrictions.eq("status", Constants.ACTIVE));
			criteria.add(Restrictions.eq("customerEntity.status",Constants.ACTIVE));
			criteria.addOrder(Order.asc("programName"));
			customerDetailsEntities = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.info("Exception in CustomerRepositoryImpl.getProgramDetailsList(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return customerDetailsEntities;
	}

	/**
	 * This method will getProgTemplateDetails
	 * 
	 * @param programTemplateModel
	 * @return List<ProgramTemplateEntity>
	 */
	@Override
	public List<ProgramTemplateEntity> getProgTemplateDetails(ProgramTemplateModel programTemplateModel) {
		logger.info("CustomerRepositoryImpl.getProgTemplateDetails() called.. ");
		List<ProgramTemplateEntity> programTemplateEntities = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<ProgramTemplateEntity> query = builder.createQuery(ProgramTemplateEntity.class);
			Root<ProgramTemplateEntity> root = query.from(ProgramTemplateEntity.class);
			query.select(root);
			if (CommonUtil.isValidObject(programTemplateModel)
					&& CommonUtil.isValidObject(programTemplateModel.getId())) {
				query.where(builder.equal(root.get("id"), programTemplateModel.getId()));
			}
			if (CommonUtil.isValidObject(programTemplateModel)
					&& CommonUtil.isValidObject(programTemplateModel.getProgramDetailsEntity())) {
				query.where(builder.equal(root.get("programDetailsEntity"),
						programTemplateModel.getProgramDetailsEntity().getId()));
			}
			if (CommonUtil.isValidObject(programTemplateModel)
					&& CommonUtil.isValidObject(programTemplateModel.getLabel())) {
				query.where(builder.equal(root.get("label"), programTemplateModel.getLabel()));
			}
			if (CommonUtil.isValidObject(programTemplateModel)
					&& CommonUtil.isValidObject(programTemplateModel.getProgramDetailsEntity())
					&& CommonUtil.isValidObject(programTemplateModel)
					&& CommonUtil.isValidObject(programTemplateModel.getLabel())) {
				query.where(builder.and(
						builder.equal(root.get("programDetailsEntity"),
								programTemplateModel.getProgramDetailsEntity().getId()),
						builder.equal(root.get("label"), programTemplateModel.getLabel())));
			}
			TypedQuery<ProgramTemplateEntity> queryResult = entityManager.createQuery(query);
			programTemplateEntities = queryResult.getResultList();
		} catch (Exception e) {
			logger.info("Exception in CustomerRepositoryImpl.getProgTemplateDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return programTemplateEntities;
	}

	/**
	 * This method will saveProgramGenerateFileEntity
	 * 
	 * @param programGenerateFileEntity
	 * @return boolean
	 */
	@Override
	public boolean saveProgramGenerateFileEntity(ProgramGenerateFileEntity programGenerateFileEntity) {
		logger.info("CustomerRepositoryImpl.saveProgramGenerateFileEntity()");
		boolean status = false;
		try {
			entityManager.merge(programGenerateFileEntity);
			status = true;
		} catch (Exception e) {
			logger.info("Exception in CustomerRepositoryImpl.saveProgramGenerateFileEntity(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This method will duplicateProgramGenerateFileDetails
	 * 
	 * @param customerDetailId
	 * @return boolean
	 */
	@SuppressWarnings("deprecation")
	@Override
	public boolean duplicateProgramGenerateFileDetails(Integer customerDetailId) {
		logger.info(
				"CustomerRepositoryImpl.duplicateProgramGenerateFileDetails() customerDetailId:" + customerDetailId);
		boolean status = false;
		try {
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(ProgramGenerateFileEntity.class);
			criteria.createAlias("programDetailsEntity", "programDetailsEntity");
			criteria.add(Restrictions.eq("programDetailsEntity.id", customerDetailId));
			criteria.setProjection(Projections.rowCount());
			Long duplicatecount = (Long) criteria.uniqueResult();
			if (duplicatecount.intValue() > 0) {
				status = true;
			}

		} catch (Exception e) {
			logger.info("Exception in CustomerRepositoryImpl.duplicateProgramGenerateFileDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This method will getProgramGenerateFileDetails
	 * 
	 * @param programId
	 * @return boolean
	 */
	@Override
	public List<ProgramGenerateFileEntity> getProgramGenerateFileDetails(Integer programId) {
		logger.info("CustomerRepositoryImpl.getProgramGenerateFileDetails() called.. programId: " + programId);
		List<ProgramGenerateFileEntity> programGenerateFileEntities = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<ProgramGenerateFileEntity> query = builder.createQuery(ProgramGenerateFileEntity.class);
			Root<ProgramGenerateFileEntity> root = query.from(ProgramGenerateFileEntity.class);
			query.select(root);
			if (CommonUtil.isValidObject(programId)) {
				query.where(builder.equal(root.get("programDetailsEntity"), programId));
			}
			TypedQuery<ProgramGenerateFileEntity> queryResult = entityManager.createQuery(query);
			programGenerateFileEntities = queryResult.getResultList();
		} catch (Exception e) {
			logger.info("Exception in CustomerRepositoryImpl.getProgramGenerateFileDetails() : "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return programGenerateFileEntities;
	}

	/**
	 * This method will getComboBoxList
	 * 
	 * @return List<SchedulingReportsTemplateEntity>
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<SchedulingReportsTemplateEntity> getComboBoxList() {
		List<SchedulingReportsTemplateEntity> schedulingReportsTemplateEntity = null;
		try {
			Criteria criteriaTotList = entityManager.unwrap(Session.class)
					.createCriteria(SchedulingReportsTemplateEntity.class);
			schedulingReportsTemplateEntity = criteriaTotList
					.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.info(
					"Exception in CustomerRepositoryImpl.getComboBoxList(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return schedulingReportsTemplateEntity;
	}

	/**
	 * This method will saveSchedulingTemplate
	 * 
	 * @param schedulingReportsTemplateEntity
	 * @return boolean
	 */
	@Override
	public boolean saveSchedulingTemplate(SchedulingReportsTemplateEntity schedulingReportsTemplateEntity) {
		boolean status = false;
		try {
			entityManager.merge(schedulingReportsTemplateEntity);
			status = true;
		} catch (Exception e) {
			logger.info("Exception in CustomerRepositoryImpl.saveSchedulingTemplate(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	
	
	/**
	 * This method will duplicateProgaramTemplate
	 * 
	 * @return List<OvGeneralEntity>
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public OvGeneralEntity getOvlabelTemplate(String label) {
		OvGeneralEntity objOvGeneralEntity = null;
		try {
			Criteria criteriaTotList = entityManager.unwrap(Session.class).createCriteria(OvGeneralEntity.class);
			if(StringUtils.isNotEmpty(label)){
				criteriaTotList.add(Restrictions.eq("label", label));
			}
			List<OvGeneralEntity> objOvTemplateEntityList = criteriaTotList.list();
			if(!ObjectUtils.isEmpty(objOvTemplateEntityList))
			{
				objOvGeneralEntity=objOvTemplateEntityList.get(0);
			}
		} catch (Exception e) {
			logger.info(
					"Exception in CustomerRepositoryImpl.getOvTemplate(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objOvGeneralEntity;
	}
	
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<SnrGeneralEntity> getSnrGeneralConfigList() {
		List<SnrGeneralEntity> SnrGeneralEntity = null;
		try {
			Criteria criteriaTotList = entityManager.unwrap(Session.class)
					.createCriteria(SnrGeneralEntity.class);
			SnrGeneralEntity = criteriaTotList
					.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.info(
					"Exception in DuoGeneralConfigRepositoryImpl.getDuoGeneralConfigList(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return SnrGeneralEntity;
	}

}
