package com.smart.rct.common.repositoryImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.smart.rct.common.entity.IPPLANEntity;
//import com.smart.rct.common.entity.MMEIPEntity;
import com.smart.rct.common.entity.MmeIpEntity;
import com.smart.rct.common.entity.RFDBEntity;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.common.repository.RFDBRepository;
import com.smart.rct.common.service.CustomerService;

//@Transactional
@Repository
public class RFDBRepositoryImpl implements RFDBRepository {
	final static Logger logger = LoggerFactory.getLogger(RFDBRepositoryImpl.class);

	@Autowired
	CustomerService customerService;
	
	@SuppressWarnings("unused")
	@Override
	public List<String> getMMEData(List<String> neLists, String condition, String entity, String columnName) {
		List<String> rFDBEntity = null;
		try {
			String query = null;
			Map<String, String> map = new HashMap<>();
			map.put("Market", "Market");
			map.put("USM", "USM");
			map.put("VERSION", "Version");
			map.put("eNB_Name", "\"eNB Name\"");
			map.put("Samsung_eNB_ID", "\"Samsung eNB ID\"");
			map.put("Cell_ID", "\"Cell ID\"");
			map.put("TAC", "TAC");
			map.put("PCI", "PCI");
			map.put("RACH", "RACH");
			map.put("BandName", "\"Band Name\"");
			map.put("Bandwidth", "\"Bandwidth (MHz)\"");
			map.put("EARFCN_DL", "\"EARFCN DL\"");
			map.put("EARFCN_UL", "\"EARFCN UL\"");
			map.put("Output_Power", "\"Output Power (dBm)\"");
			map.put("Tx_Diversity", "\"Tx Diversity\"");
			map.put("Rx_Diveristy", "\"Rx Diversity\"");
			map.put("Electrical_Tilt", "\"Electrical Tilt\"");
			map.put("RRH_Type", "\"RRH Type(after)\"");

			map.put("Card_Count_per_eNB", "\"Card Count per eNB\"");//
			map.put("Deployment", "Deployment");
			map.put("RRH_Code", "\"RRH_CODE(after)\"");
			map.put("Market_CLLI_Code", "\"Market CLLI Code\"");
			map.put("aliasName", "aliasName");
			map.put("antennaPathDelayDL", "antennaPathDelayDL");
			map.put("antennaPathDelayUL", "antennaPathDelayUL");
			map.put("antennaPathDelayDLm", "\"antennaPathDelayDL (m)\"");
			map.put("antennaPathDelayULm", "\"antennaPathDelayUL (m)\"");
			map.put("DAS_OUTPUT_POWER", "\"DAS Output Power\"");
			map.put("DAS", "DAS");

			map.put("NB_IoT_TAC", "\"NBIoT TAC\"");//
			map.put("PreambleFormat_prachIndex", "\"PRACH CONFIG INDEX\"");//
			map.put("pa", "pa");
			map.put("pb", "pb");
			map.put("SDL", "SDL");
			map.put("prachCS", "ZCZC");//
			map.put("CBRS_user_id", "\"CBRS user-id\"");
			map.put("CBRS_FCC_ID", "\"CBRS Fcc-Id\"");
			map.put("max_eirp_threshold", "\"max eirp threshold\"");
			map.put("Preferred_Earfcn", "\"Preferred Earfcn\"");
			map.put("CBSD_Category", "\"CBSD Category\"");
			map.put("RU_port", "\"RU Port ID\"");
			
			map.put("CRPIPortID", "\"CPRI Port ID\"");
			map.put("ulComp", "\"UL COMP\"");
			map.put("mcType", "\"MULTI CARRIER TYPE\"");
			map.put("lCCCard", "\"LCC Card\"");//
			map.put("prachConfigIndex", "\"PRACH CONFIG INDEX (new)\"");

			map.put("DSP_ID", "\"DSP Index\"");//
			map.put("DSP_CELL_INDEX", "\"DSP Cell Index\"");
			map.put("ANTENNA_GAIN_DBI", "\"CBRS/LAA Antenna Gain(dBi)\"");
			map.put("X_Pole_Antenna", "\"X Pole Antenna\"");
			map.put("USM_IP", "USM_IP");
			map.put("nbIOT", "NBIoT");
			map.put("eMTC", "eMTC");
			map.put("dSS", "DSS");
			map.put("dSSScenario", "\"DSS Scenario\"");
			map.put("release_version","\"Release Version\"");
			map.put("ne_version", "\"NE Version\"");
			map.put("adminiState", "\"administrative-state\"");
			map.put("Network", "\"Network\"");
			
			Map<String, String> map1 = new HashMap<>();
			map1.put("Market", "MARKET");
			map1.put("eNB_Name", "ENB_NAME");
			map1.put("eNB_ID", "ENB_ID");
			map1.put("VLAN", "VLAN");
			map1.put("eNB_OAM_VLAN", "VLAN_ID");
			map1.put("vlanprefix", "\"ENB_OAM/S&B_VLAN_PREFIX\"");
			map1.put("oamIP", "\"ENB_OAM_IP&eNB_S&B_IP\"");
			map1.put("oamGatewayIP", "\"OAM_GATEWAY_IP/ENB_S&B_GATEWAY_IP\"");

			Map<String, String> map2 = new HashMap<>();
			map2.put("Market_Prefix", "\"Market Prefix\"");
			map2.put("MME_IP", "\"MME IP\"");
			map2.put("MME_INDEX","\"MME_INDEX\"");
			
			
			String neList = String.join(",", neLists);
			if (entity.equals("RFDBEntity")) {
				
				if(columnName.equals("Bandwidth(MHz)")){
					columnName = "Bandwidth";
				}
				if(columnName.equals("antennaPathDelayDL(m)")){
					columnName = "antennaPathDelayDLm";
				}if(columnName.equals("NB-IoT_TAC")){
					columnName = "NB_IoT_TAC";
				}if(columnName.equals("antennaPathDelayUL(m)")){
					columnName = "antennaPathDelayULm";
				}if(columnName.equals("Output_Power(dBm)")){
					columnName = "Output_Power";
				}
				
				query = "select " + map.get(columnName) + " as " + columnName
						+ " from VZW_4G_CIQ_MAIN where "	+ "\"Samsung eNB ID\"" + " in(" + neList + ")";
				logger.error("Query for VZW_4G_CIQ_MAIN " + query);

				// rFDBEntity = getData(query, map.get(columnName));

			} else if (entity.equals("IPPLANEntity")) {
				query = "select " + map1.get(columnName) + " as " + columnName
						+ " from VZW_4G_CIQ_IP_PLAN where ENB_ID in(" + neList + ")";
				// rFDBEntity = getData(query, map1.get(columnName));
				logger.error("Query for VZW_4G_CIQ_IP_PLAN " + query);

			} else if (entity.equals("MMEIPEntity")) {
				query = "select " + map2.get(columnName) + " as " + columnName
						+ " from VZW_4G_CIQ_MME where "+ "\"Market Prefix\"" +" in(" + neList + ")";
				// rFDBEntity = getData(query, map2.get(columnName));
				logger.error("Query for VZW_4G_CIQ_MME " + query);

			}

			rFDBEntity = getData(query, columnName);

		} catch (Exception e) {
			logger.info(
					"Exception in getMMEData(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			// entityManager.flush();
			// entityManager.clear();
		}
		return rFDBEntity;
	}

	public List<String> getData(String query, String columnName) {

		DataSource dataSource = getDataSource();

		JdbcTemplate jdbc = new JdbcTemplate(dataSource);
		ResultSetExtractor<List<String>> ex = new ResultSetExtractor<List<String>>() {

			@Override
			public List<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<String> data = new ArrayList<>();
				while (rs.next()) {
					//System.out.println(rs.getString(columnName));
					data.add(rs.getString(columnName));
				}
				return data;
			}
		};

		return jdbc.query(query, ex);
	}

	public DataSource getDataSource() {
		DriverManagerDataSource ds = new DriverManagerDataSource();
		String url=null;
		String username=null;
		String password=null;
		List<ProgramTemplateModel> configDetailModelList = new ArrayList<ProgramTemplateModel>();
		configDetailModelList = customerService.getOvTemplateDetails(configDetailModelList, "general");
		for (ProgramTemplateModel template : configDetailModelList) {
			if (template.getLabel().equals("RFDB URL"))
				url = template.getValue();
			if (template.getLabel().equals("RFDB USER NAME"))
				username = template.getValue();
			if (template.getLabel().equals("RFDB PASSWORD"))
				password = template.getValue();
		}
		ds.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		ds.setUrl(url);
		ds.setUsername(username);
		ds.setPassword(password);
		return ds;
	}

}
