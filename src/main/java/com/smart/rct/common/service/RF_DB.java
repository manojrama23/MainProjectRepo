package com.smart.rct.common.service;

import java.util.List;

public interface RF_DB {

	List<String> getMMEIPVal(List<String> neLists, String condition, String entity, String columnName);

}
