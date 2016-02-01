package yushijinhun.authlibagent.web.yggdrasil;

import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import yushijinhun.authlibagent.model.GameProfile;

public interface ResponseSerializer {

	JSONArray serializeMap(Map<String, String> map, boolean sign);

	JSONObject serializeGameProfile(GameProfile profile, boolean withProperties);

	JSONObject serializeAuthenticateResponse(AuthenticateResponse auth, boolean withProfiles);

}
