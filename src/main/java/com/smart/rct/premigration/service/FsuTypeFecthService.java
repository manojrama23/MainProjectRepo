package com.smart.rct.premigration.service;

import org.json.simple.JSONObject;

import com.smart.rct.premigration.entity.NeMappingEntity;

public interface FsuTypeFecthService {

	JSONObject getFSUType(NeMappingEntity neMappingEntity, String user, String fsuIp);

}
