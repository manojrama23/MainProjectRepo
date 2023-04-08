package com.smart.rct.usermanagement.repositoryImpl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.usermanagement.models.UserDetailsModel;
import com.smart.rct.usermanagement.repository.UserDetailsRepository;

@Transactional
@Repository
public class UserDetailsRepositoryImpl implements UserDetailsRepository {

	final static Logger logger = LoggerFactory.getLogger(UserDetailsRepositoryImpl.class);

	@PersistenceContext
	EntityManager entityManager;

	/**
	 * this method will create the User Details.
	 * 
	 * @param userEntity
	 * @return boolean
	 */
	@Override
	public boolean createUser(UserDetailsEntity userEntity) {
		boolean status = false;
		try {
			entityManager.merge(userEntity);
			status = true;
		} catch (Exception e) {
			logger.error("Exception in UserDetailsRepositoryImpl.createUser(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * this method will delete the User Details.
	 * 
	 * @param userId
	 * @return boolean
	 */
	@Transactional
	@Override
	public boolean deleteUser(int userId) {
		boolean status = false;
		try {
			entityManager.remove(getUserById(userId));
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error("Exception in UserDetailsRepositoryImpl.deleteUser(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This method will return User List
	 * 
	 * @return List<UserDetailsEntity> userList
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public Map<String, Object> getUserList(User user, UserDetailsModel userDetailsModel, Integer customerId, int page, int count) {
		Map<String, Object> objMap = new HashMap<String, Object>();
		List<UserDetailsModel> userList = null;
		int paginationNumber = 0;
		double result = 0;
		try {
			Criteria criteriaTotList = entityManager.unwrap(Session.class).createCriteria(UserDetailsEntity.class);
			criteriaTotList.createAlias("userRoleDetailsEntity", "userRoleDetailsEntity");
			criteriaTotList.createAlias("customerEntity", "customerEntity");

			Conjunction conjunction = Restrictions.conjunction();
			Disjunction disjunctionCustomer = Restrictions.disjunction();
			Disjunction disjunctionProgram = Restrictions.disjunction();
			
			if (user != null) {
				Criterion eventuserName = Restrictions.ne("userRoleDetailsEntity.id", 1);
				conjunction.add(eventuserName);

				if (user.getRoleId() != null && user.getRoleId() > 0 && user.getRoleId() == 1) {
					Criterion eventDefaultUser = Restrictions.between("userRoleDetailsEntity.id", 2, 2);
					conjunction.add(eventDefaultUser);

				} else if (user.getRoleId() != null && user.getRoleId() > 0 && user.getRoleId() == 2) {
					Criterion eventsuperAdmin = Restrictions.between("userRoleDetailsEntity.id", 2, 5);
					conjunction.add(eventsuperAdmin);

				} else if (user.getRoleId() != null && user.getRoleId() > 0 && user.getRoleId() == 3
						&& customerId != null && customerId > 0) {
					Criterion eventcustomerId = Restrictions.eq("customerEntity.id", customerId);
					Criterion eventCustAdmin = Restrictions.between("userRoleDetailsEntity.id", 4, 5);
					Criterion eventnameUser = Restrictions.eq("userName", user.getUserName());
					// Criterion eventcreatedBy = Restrictions.eq("createdBy", user.getUserName());
					LogicalExpression orExp = Restrictions.or(eventCustAdmin, eventnameUser);
					// conjunction.add(eventCustAdmin);
					conjunction.add(orExp);
					conjunction.add(eventcustomerId);

				} else if (user.getRoleId() != null && user.getRoleId() > 0 && user.getRoleId() == 4
						&& customerId != null && customerId > 0) {
					Criterion eventcustomerId = Restrictions.eq("customerEntity.id", customerId);
					Criterion eventCustAdmin = Restrictions.between("userRoleDetailsEntity.id", 5, 5);
					Criterion eventnameUser = Restrictions.eq("userName", user.getUserName());
					// Criterion eventcreatedBy = Restrictions.eq("createdBy", user.getUserName());
					LogicalExpression orExp = Restrictions.or(eventCustAdmin, eventnameUser);
					// conjunction.add(eventCustAdmin);
					conjunction.add(orExp);
					conjunction.add(eventcustomerId);

				} else if (user.getRoleId() != null && user.getRoleId() > 0 && user.getRoleId() == 5
						&& customerId != null && customerId > 0) {
					Criterion eventcustomerId = Restrictions.eq("customerEntity.id", customerId);
					Criterion eventCustAdmin = Restrictions.eq("userRoleDetailsEntity.id", 5);
					Criterion eventnameUser = Restrictions.eq("userName", user.getUserName());
					// Criterion eventcreatedBy = Restrictions.eq("createdBy", user.getUserName());
					LogicalExpression orExp = Restrictions.or(eventCustAdmin, eventnameUser);
					// conjunction.add(eventCustAdmin);
					conjunction.add(orExp);
					conjunction.add(eventcustomerId);

				}

			}
			
			if (userDetailsModel != null) {
				if (org.apache.commons.lang.StringUtils.isNotEmpty(userDetailsModel.getUserName())) {
					Criterion eventUserName = Restrictions.ilike("userName", userDetailsModel.getUserName(), MatchMode.ANYWHERE);
					conjunction.add(eventUserName);
				}
				if (org.apache.commons.lang.StringUtils.isNotEmpty(userDetailsModel.getUserFullName())) {
					Criterion eventUserFullName = Restrictions.ilike("userFullName", userDetailsModel.getUserFullName(), MatchMode.ANYWHERE);
					conjunction.add(eventUserFullName);
				}
				if (userDetailsModel.getRoleId() != null) {
					Criterion eventRole = Restrictions.eq("userRoleDetailsEntity.id", userDetailsModel.getRoleId());
					conjunction.add(eventRole);
				}
				if (userDetailsModel.getCustomerId() != null && userDetailsModel.getCustomerId() >1 ) {
					Criterion eventCustomerName = Restrictions.eq("customerEntity.id", userDetailsModel.getCustomerId());
					Criterion eventCustomerAll = Restrictions.eq("customerEntity.id", 1);
					disjunctionCustomer.add(eventCustomerName);
					disjunctionCustomer.add(eventCustomerAll);
				}
				if (org.apache.commons.lang.StringUtils.isNotEmpty(userDetailsModel.getProgramNamehidden()) && !userDetailsModel.getProgramNamehidden().equalsIgnoreCase("All")) {
					Criterion eventProgramName = Restrictions.ilike("programName", userDetailsModel.getProgramNamehidden(), MatchMode.ANYWHERE);
					Criterion eventProgramNameAll = Restrictions.ilike("programName", "All", MatchMode.ANYWHERE);
					disjunctionProgram.add(eventProgramName);
					disjunctionProgram.add(eventProgramNameAll);
				}
				if (org.apache.commons.lang.StringUtils.isNotEmpty(userDetailsModel.getEmailId())) {
					Criterion eventEmail = Restrictions.ilike("emailId", userDetailsModel.getEmailId(), MatchMode.ANYWHERE);
					conjunction.add(eventEmail);
				}
				if (org.apache.commons.lang.StringUtils.isNotEmpty(userDetailsModel.getStatus())) {
					Criterion eventStatus = Restrictions.eq("status", userDetailsModel.getStatus());
					conjunction.add(eventStatus);
				}
				if (org.apache.commons.lang.StringUtils.isNotEmpty(userDetailsModel.getCreatedBy())) {
					Criterion eventCreatedBy = Restrictions.ilike("createdBy", userDetailsModel.getCreatedBy(), MatchMode.ANYWHERE);
					conjunction.add(eventCreatedBy);
				}
			}
			

			Projection commonProjection = Projections.projectionList()
					.add(Projections.property("userRoleDetailsEntity.id"), "roleId")
					.add(Projections.property("userRoleDetailsEntity.role"), "role")
					.add(Projections.property("customerEntity.customerName"), "customerName")
					.add(Projections.property("customerEntity.id"), "customerId")
					.add(Projections.property("userName"), "userName")
					.add(Projections.property("programName"), "programNamehidden")
					.add(Projections.property("remarks"), "remarks").add(Projections.property("status"), "status")
					.add(Projections.property("userFullName"), "userFullName")
					.add(Projections.property("emailId"), "emailId").add(Projections.property("password"), "password")
					.add(Projections.property("vpnUserName"), "vpnUserName")
					.add(Projections.property("vpnPassword"), "vpnPassword")
					.add(Projections.property("createdBy"), "createdBy")

					.add(Projections.sqlProjection(
							"DATE_FORMAT(this_.CREATION_DATE, '%Y-%m-%d %H:%i:%s') as creationDate",
							new String[] { "creationDate" }, new Type[] { new StringType() }))
					.add(Projections.sqlProjection(
							"DATE_FORMAT(this_.LAST_LOGIN_DATE, '%Y-%m-%d %H:%i:%s') as lastLoginDate",
							new String[] { "lastLoginDate" }, new Type[] { new StringType() }))
					.add(Projections.property("id"), "id");

			criteriaTotList.setProjection(commonProjection);
			criteriaTotList.add(conjunction);
			criteriaTotList.add(disjunctionCustomer);
			criteriaTotList.add(disjunctionProgram);
			criteriaTotList.setFirstResult((page - 1) * count);
			criteriaTotList.setMaxResults(count);
			criteriaTotList.addOrder(Order.desc("creationDate"));
			userList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.setResultTransformer(new AliasToBeanResultTransformer(UserDetailsModel.class)).list();
			
			Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(UserDetailsEntity.class);
			criteriaCount.add(conjunction);
			criteriaCount.add(disjunctionCustomer);
			criteriaCount.add(disjunctionProgram);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			paginationNumber = (int) result;
			objMap.put("pageCount", paginationNumber);
			objMap.put("userList", userList);
		} catch (Exception e) {
			logger.error("Exception getUserDetails :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}

	/**
	 * this method will update user details to DBu.USER_FULL_NAME as userFullName,
	 * DATE_FORMAT(u.CREATION_DATE,'%Y-%m-%d %H:%i:%s') as creationDate,\" +
	 * \"DATE_FORMAT(u.LAST_LOGIN_DATE,'%Y-%m-%d %H:%i:%s') as
	 * lastLoginDate,u.STATUS as status,cd.CUSTOMER_NAME as customerName FROM
	 * USER_DETAILS u left join USER_ROLES r on u.ROLE_ID=r.id left join
	 * CUSTOMER_LIST cd on u.CUSTOMER_ID=cd.ID
	 * 
	 * @param updateUserEntity
	 * @return boolean
	 */
	@Override
	public boolean updateUser(UserDetailsEntity updateUserEntity) {
		boolean status = false;
		try {
			updateUserEntity.setCreationDate(new Date());
			entityManager.merge(updateUserEntity);
			status = true;

		} catch (Exception e) {
			status = false;
			logger.error("Exception in UserDetailsRepositoryImpl.updateUser(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
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
	@SuppressWarnings("deprecation")
	@Override
	public boolean duplicateUser(UserDetailsModel userUserDetailsModel) {
		boolean status = false;
		try {

			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(UserDetailsEntity.class);
			criteria.createAlias("userRoleDetailsEntity", "userRoleDetailsEntity");
			criteria.createAlias("customerEntity", "customerEntity");

			Conjunction conjunction = Restrictions.conjunction();

			if (userUserDetailsModel.getId() != null && userUserDetailsModel.getId() != 0) {
				Criterion eventuserId = Restrictions.ne("id", userUserDetailsModel.getId());
				conjunction.add(eventuserId);

				Criterion userName = Restrictions.eq("userName", userUserDetailsModel.getUserName());
				Criterion emailId = Restrictions.eq("emailId", userUserDetailsModel.getEmailId());

				LogicalExpression orExp = Restrictions.or(userName, emailId);
				conjunction.add(orExp);

			} else {
				Criterion userName = Restrictions.eq("userName", userUserDetailsModel.getUserName());
				Criterion emailId = Restrictions.eq("emailId", userUserDetailsModel.getEmailId());

				LogicalExpression orExp = Restrictions.or(userName, emailId);
				conjunction.add(orExp);

			}
			criteria.add(conjunction);
			criteria.setProjection(Projections.rowCount());
			Long duplicatecount = (Long) criteria.uniqueResult();
			if (duplicatecount.intValue() > 0) {
				status = true;
			}
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in UserDetailsRepositoryImpl.duplicateUser(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * this method will return UserDetailsEntity
	 * 
	 * @param articleId
	 * @return UserDetailsEntity
	 */
	public UserDetailsEntity getUserById(int articleId) {
		return entityManager.find(UserDetailsEntity.class, articleId);
	}

	@SuppressWarnings("deprecation")
	@Override
	public Object getPageCount(int page, int count) {
		double result = 0;
		int pagecount = 0;
		try {
			Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(UserDetailsEntity.class);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			pagecount = (int) result;
		} catch (Exception e) {
			logger.error(
					"Exception getPageCount() in UserDetailsRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		}
		return pagecount;
	}

	@SuppressWarnings("deprecation")
	@Override
	public UserDetailsEntity getUserByRole(String role) {
		UserDetailsEntity  UserDetailsEntity = null;
		try {
			Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(UserDetailsEntity.class);
			
			
			Conjunction conjunction = Restrictions.conjunction();
			Criterion eventnameUser = Restrictions.eq("userName", role);				
			conjunction.add(eventnameUser);
			criteriaCount.add(conjunction);
			UserDetailsEntity = (UserDetailsEntity) criteriaCount.uniqueResult();

		} catch (Exception e) {
			logger.error(
					"Exception getPageCount() in UserDetailsRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		}finally {
			entityManager.flush();
			entityManager.clear();
		}
		return UserDetailsEntity;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public Map<String, Object> getUserNameList(User user, UserDetailsModel userDetailsModel, Integer customerId) {
		Map<String, Object> objMap = new HashMap<String, Object>();
		List<UserDetailsModel> userList = null;
		int paginationNumber = 0;
		double result = 0;
		try {
			Criteria criteriaTotList = entityManager.unwrap(Session.class).createCriteria(UserDetailsEntity.class);
			criteriaTotList.createAlias("userRoleDetailsEntity", "userRoleDetailsEntity");
			criteriaTotList.createAlias("customerEntity", "customerEntity");

			Conjunction conjunction = Restrictions.conjunction();
			Disjunction disjunctionCustomer = Restrictions.disjunction();
			Disjunction disjunctionProgram = Restrictions.disjunction();
			
			if (user != null) {
				Criterion eventuserName = Restrictions.ne("userRoleDetailsEntity.id", 1);
				conjunction.add(eventuserName);

				if (user.getRoleId() != null && user.getRoleId() > 0 && user.getRoleId() == 1) {
					Criterion eventDefaultUser = Restrictions.between("userRoleDetailsEntity.id", 2, 2);
					conjunction.add(eventDefaultUser);

				} else if (user.getRoleId() != null && user.getRoleId() > 0 && user.getRoleId() == 2) {
					Criterion eventsuperAdmin = Restrictions.between("userRoleDetailsEntity.id", 2, 5);
					conjunction.add(eventsuperAdmin);

				} else if (user.getRoleId() != null && user.getRoleId() > 0 && user.getRoleId() == 3
						&& customerId != null && customerId > 0) {
					Criterion eventcustomerId = Restrictions.eq("customerEntity.id", customerId);
					Criterion eventCustAdmin = Restrictions.between("userRoleDetailsEntity.id", 4, 5);
					Criterion eventnameUser = Restrictions.eq("userName", user.getUserName());
					// Criterion eventcreatedBy = Restrictions.eq("createdBy", user.getUserName());
					LogicalExpression orExp = Restrictions.or(eventCustAdmin, eventnameUser);
					// conjunction.add(eventCustAdmin);
					conjunction.add(orExp);
					conjunction.add(eventcustomerId);

				} else if (user.getRoleId() != null && user.getRoleId() > 0 && user.getRoleId() == 4
						&& customerId != null && customerId > 0) {
					Criterion eventcustomerId = Restrictions.eq("customerEntity.id", customerId);
					Criterion eventCustAdmin = Restrictions.between("userRoleDetailsEntity.id", 5, 5);
					Criterion eventnameUser = Restrictions.eq("userName", user.getUserName());
					// Criterion eventcreatedBy = Restrictions.eq("createdBy", user.getUserName());
					LogicalExpression orExp = Restrictions.or(eventCustAdmin, eventnameUser);
					// conjunction.add(eventCustAdmin);
					conjunction.add(orExp);
					conjunction.add(eventcustomerId);

				} else if (user.getRoleId() != null && user.getRoleId() > 0 && user.getRoleId() == 5
						&& customerId != null && customerId > 0) {
					Criterion eventcustomerId = Restrictions.eq("customerEntity.id", customerId);
					Criterion eventCustAdmin = Restrictions.eq("userRoleDetailsEntity.id", 5);
					Criterion eventnameUser = Restrictions.eq("userName", user.getUserName());
					// Criterion eventcreatedBy = Restrictions.eq("createdBy", user.getUserName());
					LogicalExpression orExp = Restrictions.or(eventCustAdmin, eventnameUser);
					// conjunction.add(eventCustAdmin);
					conjunction.add(orExp);
					conjunction.add(eventcustomerId);

				}

			}
			
			if (userDetailsModel != null) {
				if (org.apache.commons.lang.StringUtils.isNotEmpty(userDetailsModel.getUserName())) {
					Criterion eventUserName = Restrictions.ilike("userName", userDetailsModel.getUserName(), MatchMode.ANYWHERE);
					conjunction.add(eventUserName);
				}
				if (org.apache.commons.lang.StringUtils.isNotEmpty(userDetailsModel.getUserFullName())) {
					Criterion eventUserFullName = Restrictions.ilike("userFullName", userDetailsModel.getUserFullName(), MatchMode.ANYWHERE);
					conjunction.add(eventUserFullName);
				}
				if (userDetailsModel.getRoleId() != null) {
					Criterion eventRole = Restrictions.eq("userRoleDetailsEntity.id", userDetailsModel.getRoleId());
					conjunction.add(eventRole);
				}
				if (userDetailsModel.getCustomerId() != null && userDetailsModel.getCustomerId() >1 ) {
					Criterion eventCustomerName = Restrictions.eq("customerEntity.id", userDetailsModel.getCustomerId());
					Criterion eventCustomerAll = Restrictions.eq("customerEntity.id", 1);
					disjunctionCustomer.add(eventCustomerName);
					disjunctionCustomer.add(eventCustomerAll);
				}
				if (org.apache.commons.lang.StringUtils.isNotEmpty(userDetailsModel.getProgramNamehidden()) && !userDetailsModel.getProgramNamehidden().equalsIgnoreCase("All")) {
					Criterion eventProgramName = Restrictions.ilike("programName", userDetailsModel.getProgramNamehidden(), MatchMode.ANYWHERE);
					Criterion eventProgramNameAll = Restrictions.ilike("programName", "All", MatchMode.ANYWHERE);
					disjunctionProgram.add(eventProgramName);
					disjunctionProgram.add(eventProgramNameAll);
				}
				if (org.apache.commons.lang.StringUtils.isNotEmpty(userDetailsModel.getEmailId())) {
					Criterion eventEmail = Restrictions.ilike("emailId", userDetailsModel.getEmailId(), MatchMode.ANYWHERE);
					conjunction.add(eventEmail);
				}
				if (org.apache.commons.lang.StringUtils.isNotEmpty(userDetailsModel.getStatus())) {
					Criterion eventStatus = Restrictions.eq("status", userDetailsModel.getStatus());
					conjunction.add(eventStatus);
				}
				if (org.apache.commons.lang.StringUtils.isNotEmpty(userDetailsModel.getCreatedBy())) {
					Criterion eventCreatedBy = Restrictions.ilike("createdBy", userDetailsModel.getCreatedBy(), MatchMode.ANYWHERE);
					conjunction.add(eventCreatedBy);
				}
			}
			

			Projection commonProjection = Projections.projectionList()
					.add(Projections.property("userRoleDetailsEntity.id"), "roleId")
					.add(Projections.property("userRoleDetailsEntity.role"), "role")
					.add(Projections.property("customerEntity.customerName"), "customerName")
					.add(Projections.property("customerEntity.id"), "customerId")
					.add(Projections.property("userName"), "userName")
					.add(Projections.property("programName"), "programNamehidden")
					.add(Projections.property("remarks"), "remarks").add(Projections.property("status"), "status")
					.add(Projections.property("userFullName"), "userFullName")
					.add(Projections.property("emailId"), "emailId").add(Projections.property("password"), "password")
					.add(Projections.property("vpnUserName"), "vpnUserName")
					.add(Projections.property("vpnPassword"), "vpnPassword")
					.add(Projections.property("createdBy"), "createdBy")

					.add(Projections.sqlProjection(
							"DATE_FORMAT(this_.CREATION_DATE, '%Y-%m-%d %H:%i:%s') as creationDate",
							new String[] { "creationDate" }, new Type[] { new StringType() }))
					.add(Projections.sqlProjection(
							"DATE_FORMAT(this_.LAST_LOGIN_DATE, '%Y-%m-%d %H:%i:%s') as lastLoginDate",
							new String[] { "lastLoginDate" }, new Type[] { new StringType() }))
					.add(Projections.property("id"), "id");

			criteriaTotList.setProjection(commonProjection);
			criteriaTotList.add(conjunction);
			criteriaTotList.add(disjunctionCustomer);
			criteriaTotList.add(disjunctionProgram);
			criteriaTotList.addOrder(Order.desc("creationDate"));
			userList = criteriaTotList.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.setResultTransformer(new AliasToBeanResultTransformer(UserDetailsModel.class)).list();
			
			objMap.put("userList", userList);
		} catch (Exception e) {
			logger.error("Exception getUserDetails :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}
	
}
