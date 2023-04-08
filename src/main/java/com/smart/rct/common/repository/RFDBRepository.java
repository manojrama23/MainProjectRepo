package com.smart.rct.common.repository;

import java.util.List;

public interface RFDBRepository {


	List<String> getMMEData(List<String> neList, String condition,String entity, String columnName);


}
