package yushijinhun.authlibagent.dao;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Resource;
import org.json.JSONObject;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import yushijinhun.authlibagent.util.UUIDUtils;

@Component
public class ProfileCacheRepositoryImpl implements ProfileCacheRepository {

	private static final String PREFIX_PROFILE = "P";

	@Resource(name = "redisTemplate")
	private ValueOperations<String, String> valOps;

	@Override
	public Optional<JSONObject> get(UUID uuid) {
		return Optional.ofNullable(valOps.get(keyProfile(uuid))).map(JSONObject::new);
	}

	@Override
	public void put(UUID uuid, JSONObject profile) {
		valOps.set(keyProfile(uuid), profile.toString());
	}

	private String keyProfile(UUID uuid) {
		return PREFIX_PROFILE + UUIDUtils.unsign(uuid);
	}

}
