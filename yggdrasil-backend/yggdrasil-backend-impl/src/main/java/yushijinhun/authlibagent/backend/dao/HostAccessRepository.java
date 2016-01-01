package yushijinhun.authlibagent.backend.dao;

import yushijinhun.authlibagent.commons.AccessPolicy;

public interface HostAccessRepository {

	AccessPolicy getPolicy(String host);

	void setPolicy(String host, AccessPolicy policy);

}
