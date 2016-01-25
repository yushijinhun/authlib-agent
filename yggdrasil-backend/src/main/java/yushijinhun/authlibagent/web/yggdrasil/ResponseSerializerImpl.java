package yushijinhun.authlibagent.web.yggdrasil;

import static yushijinhun.authlibagent.util.UUIDUtils.unsign;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import yushijinhun.authlibagent.util.PlayerTexture;
import yushijinhun.authlibagent.web.yggdrasil.api.response.AuthenticateResponse;
import yushijinhun.authlibagent.web.yggdrasil.api.response.GameProfileResponse;

@Component
public class ResponseSerializerImpl implements ResponseSerializer {

	@Autowired
	private SignatureService signatureService;

	@Override
	public JSONArray serializeMap(Map<String, String> map, boolean sign) {
		JSONArray entries = new JSONArray();
		map.forEach((k, v) -> {
			JSONObject entry = new JSONObject();
			entry.put("name", k);
			entry.put("value", v);
			if (sign) {
				byte[] signature;
				try {
					signature = signatureService.sign(v.getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					throw new IllegalStateException("utf-8 not supported", e);
				} catch (GeneralSecurityException e) {
					throw new IllegalStateException("unable to sign", e);
				}
				entry.put("signature", Base64.getEncoder().encodeToString(signature));
			}
			entries.put(entry);
		});
		return entries;
	}

	@Override
	public JSONObject serializeGameProfile(GameProfileResponse profile, boolean withProperties) {
		JSONObject json = new JSONObject();
		json.put("id", unsign(profile.getUUID()));
		json.put("name", profile.getName());

		if (withProperties) {
			json.put("properties", serializeMap(getProfileProperties(profile), true));
		}

		return json;
	}

	private Map<String, String> getProfileProperties(GameProfileResponse profile) {
		Map<String, String> properties = new HashMap<>();
		try {
			properties.put("textures", Base64.getEncoder().encodeToString(toTexturePayload(profile).toString().getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("utf-8 not supported", e);
		}
		return properties;
	}

	private JSONObject toTexturePayload(GameProfileResponse profile) {
		JSONObject payload = new JSONObject();
		payload.put("timestamp", System.currentTimeMillis());
		payload.put("profileId", unsign(profile.getUUID()));
		payload.put("profileName", profile.getName());
		payload.put("isPublic", true);

		JSONObject textureEntries = new JSONObject();
		PlayerTexture texture = profile.getTexture();
		Map<String, String> textureProperties = toTextureProperties(texture);
		if (texture.getSkin() != null)
			textureEntries.put("SKIN", toTextureEntry(texture.getSkin(), textureProperties));
		if (texture.getCape() != null)
			textureEntries.put("CAPE", toTextureEntry(texture.getCape(), textureProperties));

		payload.put("textures", textureEntries);
		return payload;
	}

	private JSONObject toTextureEntry(String url, Map<String, String> properties) {
		JSONObject entry = new JSONObject();
		entry.put("url", url);
		entry.put("metadata", properties);
		return entry;
	}

	private Map<String, String> toTextureProperties(PlayerTexture texture) {
		Map<String, String> properties = new HashMap<>();
		properties.put("model", texture.getModel().getModelName());
		return properties;
	}

	@Override
	public JSONObject serializeAuthenticateResponse(AuthenticateResponse auth) {
		JSONObject resp = new JSONObject();
		resp.put("accessToken", auth.getAccessToken());
		resp.put("clientToken", auth.getClientToken());

		if (auth.getSelectedProfile() != null) {
			resp.put("selectedProfile", serializeGameProfile(auth.getSelectedProfile(), false));
		}

		if (auth.getProfiles() != null) {
			JSONArray profilesResp = new JSONArray();
			for (GameProfileResponse profile : auth.getProfiles()) {
				profilesResp.put(serializeGameProfile(profile, false));
			}
			resp.put("availableProfiles", profilesResp);
		}

		JSONObject userResp = new JSONObject();
		if (auth.getUserid() != null) {
			userResp.put("id", auth.getUserid());
		}
		if (auth.getProperties() != null) {
			userResp.put("properties", serializeMap(auth.getProperties(), false));
		}
		resp.put("user", userResp);

		return resp;
	}

}
