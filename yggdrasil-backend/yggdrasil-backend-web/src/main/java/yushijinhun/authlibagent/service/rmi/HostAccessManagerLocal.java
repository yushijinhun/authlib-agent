package yushijinhun.authlibagent.service.rmi;

import yushijinhun.authlibagent.api.AccessPolicy;
import yushijinhun.authlibagent.api.HostAccessManager;

public interface HostAccessManagerLocal extends HostAccessManager {

	@Override
	AccessPolicy getHostPolicy(String host);

	@Override
	void setHostPolicy(String host, AccessPolicy policy);

	@Override
	AccessPolicy getDefaultPolicy();

	@Override
	void setDefaultPolicy(AccessPolicy policy);

}
