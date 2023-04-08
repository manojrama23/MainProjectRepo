package com.smart.rct.common.serviceImpl;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import com.jcraft.jsch.Logger;
import com.smart.rct.common.repository.RFDBRepository;
import com.smart.rct.common.service.RF_DB;
import com.smart.rct.premigration.serviceImpl.FetchProcessServiceImpl;

@Service
public class RF_DBServiceImpl implements RF_DB{
	final static Logger logger = LoggerFactory.getLogger(RF_DBServiceImpl.class);

	@Autowired
	RFDBRepository rFDBRepository;

	@Override
	public List<String> getMMEIPVal(List<String> neLists,String condition,String entity, String columnName) {
		List<String> mmeModel= null;
		mmeModel = rFDBRepository.getMMEData(neLists,condition,entity,columnName);
		logger.error("RFDB MMEModel: "+ mmeModel);
		return mmeModel;
	}
}


