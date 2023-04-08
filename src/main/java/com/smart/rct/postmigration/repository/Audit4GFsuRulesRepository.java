package com.smart.rct.postmigration.repository;

import com.smart.rct.postmigration.entity.Audit4GFsuRulesEntity;

public interface Audit4GFsuRulesRepository {
	Audit4GFsuRulesEntity getAudit4GFsuRulesEntityById(int auditRuleId);
}
