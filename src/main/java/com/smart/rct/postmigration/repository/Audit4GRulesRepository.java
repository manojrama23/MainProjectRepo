package com.smart.rct.postmigration.repository;

import com.smart.rct.postmigration.entity.Audit4GRulesEntity;

public interface Audit4GRulesRepository {

	Audit4GRulesEntity getAudit4GRulesEntityById(int auditRuleId);

}
