package yushijinhun.authlibagent.backend.service;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import yushijinhun.authlibagent.backend.api.HostAccessManager;
import yushijinhun.authlibagent.backend.dao.HostAccessRepository;
import yushijinhun.authlibagent.commons.AccessPolicy;

@Component("host_access_manager")
public class HostAccessManagerImpl implements HostAccessManager {

	private static final String DEFAULT_POLICY_KEY = "default_policy";
	private static final AccessPolicy DEFAULT_POLICY = AccessPolicy.ALLOW;

	@Qualifier("host_access_repository")
	private HostAccessRepository repo;

	@Override
	public AccessPolicy getHostPolicy(String host) {
		if (host == null || DEFAULT_POLICY_KEY.equals(host)) {
			return null;
		}
		return repo.getPolicy(host);
	}

	@Override
	public void setHostPolicy(String host, AccessPolicy policy) {
		Objects.requireNonNull(host);
		if (DEFAULT_POLICY_KEY.equals(host)) {
			return;
		}
		repo.setPolicy(host, policy);
	}

	@Override
	public AccessPolicy getDefaultPolicy() {
		AccessPolicy policy = repo.getPolicy(DEFAULT_POLICY_KEY);
		if (policy == null) {
			repo.setPolicy(DEFAULT_POLICY_KEY, DEFAULT_POLICY);
			return DEFAULT_POLICY;
		}
		return policy;
	}

	@Override
	public void setDefaultPolicy(AccessPolicy policy) {
		Objects.requireNonNull(policy);
		repo.setPolicy(DEFAULT_POLICY_KEY, policy);
	}

}
