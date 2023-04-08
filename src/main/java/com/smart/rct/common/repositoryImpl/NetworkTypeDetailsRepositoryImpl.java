package com.smart.rct.common.repositoryImpl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.NetworkTypeDetailsEntity;
import com.smart.rct.common.repository.NetworkTypeDetailsRepository;
import com.smart.rct.constants.Constants;
import com.smart.rct.exception.RctException;
import com.smart.rct.usermanagement.repositoryImpl.UserDetailsRepositoryImpl;

@Repository
@EnableTransactionManagement
@Transactional()
public class NetworkTypeDetailsRepositoryImpl implements NetworkTypeDetailsRepository {

	final static Logger logger = LoggerFactory.getLogger(UserDetailsRepositoryImpl.class);

	@PersistenceContext
	EntityManager entityManager;

	/**
	 * This method will Save the NetworkType Details and return Status
	 * 
	 * @param NetworkTypeDetailsEntity
	 * @return boolean
	 */
	@Override
	public boolean saveNetworkTypeDetails(NetworkTypeDetailsEntity networkTypeDetailsEntity) {
		boolean status = false;
		try {
			entityManager.merge(networkTypeDetailsEntity);
			status = true;
		} catch (Exception e) {
			logger.error("Exception in  NetworkTypeDetailsRepositoryImpl.createNetworkTypeDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * This method will return nwTypeDetialEntityList
	 * 
	 * @param addAllRecord
	 * @return NetworkTypeDetailsEntity
	 * 
	 */
	@Override
	public List<NetworkTypeDetailsEntity> getNwTypeDetails(boolean addAllRecord) {

		List<NetworkTypeDetailsEntity> nwTypeDetialEntityList = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<NetworkTypeDetailsEntity> query = builder.createQuery(NetworkTypeDetailsEntity.class);
			Root<NetworkTypeDetailsEntity> root = query.from(NetworkTypeDetailsEntity.class);
			query.select(root);
			if (!addAllRecord) {
				query.where(builder.notEqual(root.get("id"), 1));
			}
			TypedQuery<NetworkTypeDetailsEntity> queryResult = entityManager.createQuery(query);
			nwTypeDetialEntityList = queryResult.getResultList();
		} catch (Exception e) {
			logger.info("Exception in NetworkTypeDetailsRepositoryImpl.getNwTypeDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return nwTypeDetialEntityList;
	}

	/**
	 * This method will delete NetworkTypeDetiuals Details by id
	 * 
	 * @param networkTypeId
	 * @return boolean
	 * @throws RctException
	 */
	@Override
	public boolean deleteNetworkTypeDetials(int networkTypeId) {
		boolean status = false;
		try {
			entityManager.remove(getNetworkTypeById(networkTypeId));
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error("Exception in  NetworkTypeDetailsRepositoryImpl.deleteNwtypeDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * this method will return NetworkTypeDetailsEntity by Id
	 * 
	 * @param networkTypeId
	 * @return nwTypeDetialEntity
	 */
	@Override
	public NetworkTypeDetailsEntity getNetworkTypeById(int networkTypeId) {
		logger.info("CustomerRepositoryImpl.getNetworkTypeById() networkTypeId: " + networkTypeId);
		return entityManager.find(NetworkTypeDetailsEntity.class, networkTypeId);
	}

	/**
	 * this method will return NetworkTypeDetailsEntity by Name
	 * 
	 * @param networkType
	 * @return NetworkTypeDetailsEntity
	 */
	@Override
	public NetworkTypeDetailsEntity getNetworkTypeByName(String networkType) {
		logger.info("CustomerRepositoryImpl.getNetworkTypeByName() networkType: " + networkType);

		NetworkTypeDetailsEntity nwTypeDetialEntity = null;

		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<NetworkTypeDetailsEntity> query = cb.createQuery(NetworkTypeDetailsEntity.class);
			Root<NetworkTypeDetailsEntity> root = query.from(NetworkTypeDetailsEntity.class);
			query.select(root).where(cb.equal(root.get("networkType"), networkType));

			TypedQuery<NetworkTypeDetailsEntity> typedQuery = entityManager.createQuery(query);
			nwTypeDetialEntity = typedQuery.getSingleResult();
		} catch (Exception e) {
			logger.info("Exception in getNwTypeDetails : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return nwTypeDetialEntity;
	}

	/**
	 * this method will return NetworkTypeDetailsEntity by CustomerId
	 * 
	 * @param customerId
	 * @return customerDetailsEntities
	 */
	@Override
	public List<CustomerDetailsEntity> getNwTypeDetailsByCustomerId(int customerId) {
		List<CustomerDetailsEntity> customerDetailsEntities = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<CustomerDetailsEntity> query = builder.createQuery(CustomerDetailsEntity.class);
			Root<CustomerDetailsEntity> root = query.from(CustomerDetailsEntity.class);
			query.select(root);
			query.where(builder.and(builder.equal(root.get("customerId"), customerId),
					builder.equal(root.get("status"), Constants.ACTIVE)));
			TypedQuery<CustomerDetailsEntity> queryResult = entityManager.createQuery(query);
			customerDetailsEntities = queryResult.getResultList();
		} catch (Exception e) {
			logger.info("Exception in NetworkTypeDetailsRepositoryImpl.getNwTypeDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return customerDetailsEntities;
	}
}
