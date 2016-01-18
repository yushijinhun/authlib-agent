package yushijinhun.authlibagent.web;

import java.util.Map;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;
import yushijinhun.authlibagent.api.web.response.AuthenticateResponse;
import yushijinhun.authlibagent.api.web.response.GameProfileResponse;

public interface ResponseSerializer {

	JSONArray serializeMap(Map<String, String> map, boolean sign);

	JSONObject serializeGameProfile(GameProfileResponse profile, boolean withProperties);

	JSONObject serializeAuthenticateResponse(AuthenticateResponse auth, UUID clientToken);

}
