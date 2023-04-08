package com.smart.rct.postmigration.dto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.smart.rct.common.entity.ReportsEntity;
import com.smart.rct.constants.Constants;
import com.smart.rct.postmigration.models.ReportsModel;
import com.smart.rct.premigration.entity.GenerateInfoAuditEntity;
import com.smart.rct.premigration.models.GenerateInfoAuditModel;
import com.smart.rct.util.CommonUtil;

@Component
public class ReportsDto {
	final static Logger logger = LoggerFactory.getLogger(ReportsDto.class);

	public JSONObject getReportsDetailsModel(ReportsEntity reportsEntity,String pgname, List<String> filter) {
		
		Map<String,Map<String,String>> map = new LinkedHashMap<String,Map<String,String>>();
		JSONObject resultMap= new JSONObject();
		try {
			String str[]=Constants.REPORTS_COLUMNS_ALL;

			Map<String,String> mapp = new LinkedHashMap<String,String>();
			
			if (reportsEntity != null){


					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[0]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getStartDate(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("Start Date", mapp);
				

					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[1]);
					mapp.put("headerValue",reportsEntity.getMarket());
					map.put("Market", mapp);
				
				
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[2]);
					mapp.put("headerValue",reportsEntity.getCiqName());
					map.put("Ciq Name", mapp);
				
				
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[3]);
					mapp.put("headerValue",reportsEntity.getNeName());
					map.put("Ne Name", mapp);
				
				
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[4]);
					mapp.put("headerValue",reportsEntity.getEnbId());
					map.put("Ne Id", mapp);
				
				
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[5]);
					mapp.put("headerValue",reportsEntity.getUserName());
					map.put("Engineer", mapp);
					
					 
				if(pgname.equals("") || !pgname.contains("DSS")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[6]);
					mapp.put("headerValue",reportsEntity.getPreMigEnvStatus());
					map.put("PreMig Env", mapp);
					}
				
				if(pgname.equals("") || !pgname.contains("DSS")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[7]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getPreMigEnvGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("PreMig Env Gen Date", mapp);
					}
				
				if(pgname.equals("") || !pgname.contains("DSS")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[8]);
					mapp.put("headerValue",reportsEntity.getPreMigGrowStatus());
					map.put("PreMig Grow", mapp);
					}
				if(pgname.equals("") || !pgname.contains("DSS")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[9]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getPreMigGrowGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("PreMig Grow Gen Date", mapp);
					}
				
				if(pgname.equals("") || !pgname.contains("DSS")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[10]);
					mapp.put("headerValue",reportsEntity.getPreMigCommStatus());
					map.put("PreMig Commission", mapp);
				}
				if(pgname.equals("") || !pgname.contains("DSS")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[11]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getPreMigCommGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("PreMig Commission Gen Date", mapp);
					}
			
				if(pgname.equals("") || pgname.contains("MM")) {
						mapp = new HashMap<String,String>();
						mapp.put("headerName",str[12]);
						mapp.put("headerValue",reportsEntity.getPreMigEndcStatus());
						map.put("PreMig Endc", mapp);
					}
				if(pgname.equals("") || pgname.contains("MM")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[13]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getPreMigEndcGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("PreMig Endc Gen Date", mapp);
					}
				
				if(pgname.equals("") || pgname.contains("DSS")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[14]);
					mapp.put("headerValue",reportsEntity.getPreMigRfScriptStatus());
					map.put("PreMig RF Script", mapp);
				}
				if(pgname.equals("") || pgname.contains("DSS")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[15]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getPreMigRfGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("PreMig RF Script Gen Date", mapp);
					}
				
				if(pgname.equals("") || !pgname.contains("DSS")) {
						mapp = new HashMap<String,String>();
						mapp.put("headerName",str[16]);
						mapp.put("headerValue",reportsEntity.getNeGrowPnpStatus());
						map.put("Ne grow Pnp", mapp);
					
				}
				if(pgname.equals("") || !pgname.contains("DSS")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[17]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getNeGrowPnpgenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("Ne grow Pnp Gen Date", mapp);
					}

				if(pgname.equals("") || pgname.contains("MM")) {
						mapp = new HashMap<String,String>();
						mapp.put("headerName",str[18]);
						mapp.put("headerValue",reportsEntity.getNeGrowAuCacellStatus());
						map.put("Ne grow AucaCell", mapp);
					}
				if(pgname.equals("") || pgname.contains("MM")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[19]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getNeGrowAuCacellGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("Ne grow AucaCell Gen Date", mapp);
					}
				
				
				if(pgname.equals("") || pgname.contains("MM")) {
						mapp = new HashMap<String,String>();
						mapp.put("headerName",str[20]);
						mapp.put("headerValue",reportsEntity.getNeGrowAuStatus());
						map.put("Ne grow Au", mapp);
					}
				
				if(pgname.equals("") || pgname.contains("MM")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[21]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getNeGrowAuGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("Ne grow Au Gen Date", mapp);
					}
				
				if(pgname.equals("") || pgname.contains("USM-LIVE")) {
						mapp = new HashMap<String,String>();
						mapp.put("headerName",str[22]);
						mapp.put("headerValue",reportsEntity.getNeGrowenbStatus());
						map.put("Ne grow Enb", mapp);
					}
				if(pgname.equals("") || pgname.contains("USM-LIVE")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[23]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getNeGrowenbGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("Ne grow Enb Gen Date", mapp);
					}
				
				if(pgname.equals("") || pgname.contains("USM-LIVE")) {
						mapp = new HashMap<String,String>();
						mapp.put("headerName",str[24]);
						mapp.put("headerValue",reportsEntity.getNeGrowCellStatus());
						map.put("Ne Grow Cell", mapp);
					}
				
				if(pgname.equals("") || pgname.contains("USM-LIVE")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[25]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getNeGrowCellGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("Ne Grow Cell Gen Date", mapp);
					}
				
				if(pgname.equals("") || pgname.contains("MM")||pgname.contains("USM-LIVE")) {
						mapp = new HashMap<String,String>();
						mapp.put("headerName",str[26]);
						mapp.put("headerValue",reportsEntity.getMigCommStatus());
						map.put("Mig Commission", mapp);
					}
				
				if(pgname.equals("") || pgname.contains("MM")||pgname.contains("USM-LIVE")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[27]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getMigCommGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("Mig Commission Gen Date", mapp);
					}
				
				if(pgname.equals("") || pgname.contains("MM")) {
						mapp = new HashMap<String,String>();
						mapp.put("headerName",str[28]);
						mapp.put("headerValue",reportsEntity.getMigAcpfStatus());
						map.put("Mig Acpf", mapp);
					}
				if(pgname.equals("") || pgname.contains("MM")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[29]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getMigAcpfGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("Mig Acpf Gen Date", mapp);
					}
				
				
				if(pgname.equals("") || pgname.contains("MM")) {
						mapp = new HashMap<String,String>();
						mapp.put("headerName",str[30]);
						mapp.put("headerValue",reportsEntity.getMigCslStatus());
						map.put("Mig Csl", mapp);
					}
				
				if(pgname.equals("") || pgname.contains("MM")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[31]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getMigCslGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("Mig Csl Gen Date", mapp);
					}
				
				if(pgname.equals("") || pgname.contains("MM")) {
						mapp = new HashMap<String,String>();
						mapp.put("headerName",str[32]);
						mapp.put("headerValue",reportsEntity.getMigEndcStatus());
						map.put("Mig Endc", mapp);
					}
				if(pgname.equals("") || pgname.contains("MM")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[33]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getMigEndcGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("Mig Endc Gen Date", mapp);
					}
				
				
				if(pgname.equals("") || pgname.contains("MM")) {
						mapp = new HashMap<String,String>();
						mapp.put("headerName",str[34]);
						mapp.put("headerValue",reportsEntity.getMigAnchorStatus());
						map.put("Mig Anchor", mapp);
					}
				
				if(pgname.equals("") || pgname.contains("MM")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[35]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getMigAnchorGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("Mig Anchor Gen Date", mapp);
					}
				
				if(pgname.equals("") || pgname.contains("MM")) {
						mapp = new HashMap<String,String>();
						mapp.put("headerName",str[36]);
						mapp.put("headerValue",reportsEntity.getMigNbrStatus());
						map.put("Mig NBR", mapp);
					}
				if(pgname.equals("") || pgname.contains("MM")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[37]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getMigNbrGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("Mig NBR Gen Date", mapp);
					}
				
				
				if(pgname.equals("") || pgname.contains("USM-LIVE")) {
						mapp = new HashMap<String,String>();
						mapp.put("headerName",str[38]);
						mapp.put("headerValue",reportsEntity.getMigRfStatus());
						map.put("Mig RF", mapp);
					}
				if(pgname.equals("") || pgname.contains("USM-LIVE")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[39]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getMigRfGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("Mig RF Gen Date", mapp);
					}
				
				
				if(pgname.equals("") || pgname.contains("DSS")) {
						mapp = new HashMap<String,String>();
						mapp.put("headerName",str[40]);
						mapp.put("headerValue",reportsEntity.getMigPreCheckStatus());
						map.put("Mig PreCheck", mapp);
					}
				
				if(pgname.equals("") || pgname.contains("DSS")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[41]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getMigPreCheckGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("Mig PreCheck Gen Date", mapp);
					}
				
				if(pgname.equals("") || pgname.contains("DSS")) {
						mapp = new HashMap<String,String>();
						mapp.put("headerName",str[42]);
						mapp.put("headerValue",reportsEntity.getMigCutoverStatus());
						map.put("Mig Cutover", mapp);
					}
				if(pgname.equals("") || pgname.contains("DSS")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[43]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getMigCutoverGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("Mig Cutover Gen Date", mapp);
					}
				
				if(pgname.equals("") || pgname.contains("DSS")) {
						mapp = new HashMap<String,String>();
						mapp.put("headerName",str[44]);
						mapp.put("headerValue",reportsEntity.getMigRollbackStatus());
						map.put("Mig RollBack", mapp);
					}
				if(pgname.equals("") || pgname.contains("DSS")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[45]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getMigRollbackGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("Mig RollBack Gen Date", mapp);
					}
				
				if(pgname.equals("") || pgname.contains("MM")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[46]);
					mapp.put("headerValue",reportsEntity.getPostAuAuditStatus());
					map.put("PostMig AU", mapp);
				}
				if(pgname.equals("") || !pgname.contains("DSS")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[47]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getPostAuAuditGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("PostMig AU Gen Date", mapp);
					}
				
				if(pgname.equals("") || pgname.contains("MM")) {
						mapp = new HashMap<String,String>();
						mapp.put("headerName",str[48]);
						mapp.put("headerValue",reportsEntity.getPostEndcAuditStatus());
						map.put("PostMig ENDC", mapp);
					}
				if(pgname.equals("") || pgname.contains("MM")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[49]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getPostEndcAuditGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("PostMig ENDC Gen Date", mapp);
					}
				
				if(pgname.equals("") || pgname.contains("USM-LIVE")) {
						mapp = new HashMap<String,String>();
						mapp.put("headerName",str[50]);
						mapp.put("headerValue",reportsEntity.getPostMigAtpStatus());
						map.put("PostMig ATP", mapp);
					}
				if(pgname.equals("") || pgname.contains("USM-LIVE")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[51]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getPostMigAtpGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("PostMig ATP Gen Date", mapp);
					}
				if(pgname.equals("") || pgname.contains("USM-LIVE")) {
						mapp = new HashMap<String,String>();
						mapp.put("headerName",str[52]);
						mapp.put("headerValue",reportsEntity.getPostMigAudiStatus());
						map.put("PostMig Audit", mapp);
					}
				if(pgname.equals("") || pgname.contains("USM-LIVE")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[53]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getPostMigAudiGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("PostMig Audit Gen Date", mapp);
					}
				
				if(pgname.equals("") || pgname.contains("DSS")) {
						mapp = new HashMap<String,String>();
						mapp.put("headerName",str[54]);
						mapp.put("headerValue",reportsEntity.getPostMigVduStatus());
						map.put("PostMig VDU Audit", mapp);
					}
				if(pgname.equals("") || pgname.contains("DSS")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[55]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getPostMigVduGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("PostMig VDU Audit Gen Date", mapp);
					}
				
				if(pgname.equals("") || pgname.contains("DSS")) {
						mapp = new HashMap<String,String>();
						mapp.put("headerName",str[56]);
						mapp.put("headerValue",reportsEntity.getPostMigEnbAudiStatus());
						map.put("PostMig ENB", mapp);
					}
				if(pgname.equals("") || pgname.contains("DSS")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[57]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getPostMigEnbAudiGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("PostMig ENB Gen Date", mapp);
					}
				
				if(pgname.equals("") || pgname.contains("DSS")|| pgname.contains("MM")) {
						mapp = new HashMap<String,String>();
						mapp.put("headerName",str[58]);
						mapp.put("headerValue",reportsEntity.getPostAupfStatus());
						map.put("PostMig AUPF", mapp);
					}
				if(pgname.equals("") || pgname.contains("DSS")|| pgname.contains("MM")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[59]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getPostAupfGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("PostMig AUPF Gen Date", mapp);
					}
				if(pgname.equals("") || pgname.contains("DSS")|| pgname.contains("MM")) {
						mapp = new HashMap<String,String>();
						mapp.put("headerName",str[60]);
						mapp.put("headerValue",reportsEntity.getPostAcpfAuditStatus());
						map.put("PostMig ACPF", mapp);
					}
				if(pgname.equals("") || pgname.contains("DSS")|| pgname.contains("MM")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[61]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getPostAcpfAuditGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("PostMig ACPF Gen Date", mapp);
					}
				
				if(pgname.equals("") || pgname.contains("DSS")) {
						mapp = new HashMap<String,String>();
						mapp.put("headerName",str[62]);
						mapp.put("headerValue",reportsEntity.getPostMigFsuAuditStatus());
						map.put("PostMig FSU Audit", mapp);
					}
				if(pgname.equals("") || pgname.contains("DSS")) {
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[63]);
					mapp.put("headerValue",CommonUtil.dateToString(reportsEntity.getPostMigFsuAuditGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
					map.put("PostMig FSU Audit Gen Date", mapp);
					}
				
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[64]);
					mapp.put("headerValue",reportsEntity.getSiteDataStatus());
					map.put("Site Data", mapp);
				
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[65]);
					mapp.put("headerValue",reportsEntity.getRemarks());
					map.put("Remarks", mapp);	
				
				
					mapp = new HashMap<String,String>();
					mapp.put("headerName",str[66]);
					mapp.put("headerValue",reportsEntity.getProgrameName());
					map.put("Program Name", mapp);	
				
			}
			
			resultMap.put("reportsMap", map);
				
		}catch (Exception e) {
			logger.error("Excpetion getciqAuditDetailsModel : " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return resultMap;
	}
}

