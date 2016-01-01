package yushijinhun.authlibagent.backend.util;

import java.util.Objects;
import java.util.UUID;

public class TokenPair {

	private UUID clientToken;
	private UUID accessToken;

	public TokenPair(UUID clientToken, UUID accessToken) {
		this.clientToken = clientToken;
		this.accessToken = accessToken;
	}

	public UUID getClientToken() {
		return clientToken;
	}

	public UUID getAccessToken() {
		return accessToken;
	}

	@Override
	public int hashCode() {
		return Objects.hash(clientToken, accessToken);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof TokenPair) {
			TokenPair another = (TokenPair) obj;
			return Objects.equals(clientToken, another.clientToken) && Objects.equals(accessToken, another.accessToken);
		}
		return false;
	}

	@Override
	public String toString() {
		return "[clientToken=" + clientToken + ", accessToken=" + accessToken + "]";
	}

}
