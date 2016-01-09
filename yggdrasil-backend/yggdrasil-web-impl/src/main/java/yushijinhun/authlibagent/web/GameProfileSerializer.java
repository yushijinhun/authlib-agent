package yushijinhun.authlibagent.web;

import org.json.JSONObject;
import yushijinhun.authlibagent.api.web.response.GameProfileResponse;

public interface GameProfileSerializer {

	JSONObject serialize(GameProfileResponse profile, boolean withProperties, boolean sign);

	default JSONObject serialize(GameProfileResponse profile) {
		return serialize(profile, false, false);
	}

}
