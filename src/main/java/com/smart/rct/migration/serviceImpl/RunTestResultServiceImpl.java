package com.smart.rct.migration.serviceImpl;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.rct.migration.entity.RunTestResultEntity;
import com.smart.rct.migration.repository.RunTestResultRepository;
import com.smart.rct.migration.service.RunTestResultService;

@Service
public class RunTestResultServiceImpl implements RunTestResultService {

	private static final Logger logger = LoggerFactory.getLogger(RunTestResultServiceImpl.class);
	@Autowired
	RunTestResultRepository runTestResultRepository;

	@Override
	public boolean createRunTestResult(RunTestResultEntity runTestResultEntity) {
		try {
			runTestResultRepository.createRunTestResult(runTestResultEntity);

		} catch (Exception e) {
			logger.error("Exception RunTestResultServiceImpl in createRunTestResult() "
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return false;
	}

}