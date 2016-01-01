package yushijinhun.authlibagent.backend.service.rmi;

import yushijinhun.authlibagent.backend.api.HostAccessManager;
import yushijinhun.authlibagent.commons.AccessPolicy;

public interface HostAccessManagerLocal extends HostAccessManager {

	public static final String DEFAULT_POLICY_KEY = "default_policy";
	public static final AccessPolicy DEFAULT_POLICY = AccessPolicy.ALLOW;

	@Override
	AccessPolicy getHostPolicy(String host);

	@Override
	void setHostPolicy(String host, AccessPolicy policy);

	@Override
	AccessPolicy getDefaultPolicy();

	@Override
	void setDefaultPolicy(AccessPolicy policy);

}
