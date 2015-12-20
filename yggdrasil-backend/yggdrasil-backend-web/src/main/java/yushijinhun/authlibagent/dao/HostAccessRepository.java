package yushijinhun.authlibagent.dao;

import yushijinhun.authlibagent.api.AccessPolicy;

public interface HostAccessRepository {

	AccessPolicy getPolicy(String host);

	void setPolicy(String host, AccessPolicy policy);

}
