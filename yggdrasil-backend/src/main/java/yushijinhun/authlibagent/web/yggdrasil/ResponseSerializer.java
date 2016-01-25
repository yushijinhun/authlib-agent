package yushijinhun.authlibagent.web.yggdrasil;

import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import yushijinhun.authlibagent.web.yggdrasil.api.response.AuthenticateResponse;
import yushijinhun.authlibagent.web.yggdrasil.api.response.GameProfileResponse;

public interface ResponseSerializer {

	JSONArray serializeMap(Map<String, String> map, boolean sign);

	JSONObject serializeGameProfile(GameProfileResponse profile, boolean withProperties);

	JSONObject serializeAuthenticateResponse(AuthenticateResponse auth);

}
