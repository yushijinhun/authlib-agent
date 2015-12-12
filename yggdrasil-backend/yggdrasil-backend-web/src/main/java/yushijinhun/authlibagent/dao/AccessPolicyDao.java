package yushijinhun.authlibagent.dao;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import yushijinhun.authlibagent.api.AccessPolicy;

@Entity
public class AccessPolicyDao implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String DEFAULT_POLICY_KEY = "default_policy";

	private String host;
	private AccessPolicy policy;

	@Id
	@Column(nullable = false, unique = true)
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	public AccessPolicy getPolicy() {
		return policy;
	}

	public void setPolicy(AccessPolicy policy) {
		this.policy = policy;
	}

	@Override
	public int hashCode() {
		return Objects.hash(host);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof AccessPolicyDao) {
			AccessPolicyDao another = (AccessPolicyDao) obj;
			return Objects.equals(getHost(), another.getHost());
		}
		return false;
	}

}
