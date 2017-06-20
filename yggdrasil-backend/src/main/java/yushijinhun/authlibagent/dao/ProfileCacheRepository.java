package yushijinhun.authlibagent.dao;

import java.util.Optional;
import java.util.UUID;
import org.json.JSONObject;

public interface ProfileCacheRepository {

	Optional<JSONObject> get(UUID uuid);

	void put(UUID uuid, JSONObject profile);

}
