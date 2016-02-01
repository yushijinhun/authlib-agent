package yushijinhun.authlibagent.web.yggdrasil;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import yushijinhun.authlibagent.model.GameProfile;
import yushijinhun.authlibagent.service.SignatureService;

@Component
public class ResponseSerializerImpl implements ResponseSerializer {

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	@Autowired
	private SignatureService signatureService;

	@Value("#{config['feature.optionalSignature']}")
	private boolean optionalSignature;

	@Override
	public JSONArray serializeMap(Map<String, String> map, boolean sign) {
		JSONArray entries = new JSONArray();
		map.forEach((k, v) -> {
			JSONObject entry = new JSONObject();
			entry.put("name", k);
			entry.put("value", v);
			if (sign) {
				try {
					byte[] signature = signatureService.sign(v.getBytes("UTF-8"));
					entry.put("signature", Base64.getEncoder().encodeToString(signature));
				} catch (UnsupportedEncodingException | GeneralSecurityException e) {
					if (optionalSignature) {
						// ignore
						LOGGER.debug("unable to sign, skipping", e);
					} else {
						throw new IllegalStateException("unable to sign", e);
					}
				}
			}
			entries.put(entry);
		});
		return entries;
	}

	@Override
	public JSONObject serializeGameProfile(GameProfile profile, boolean withProperties) {
		JSONObject json = new JSONObject();
		json.put("id", profile.getUuid());
		json.put("name", profile.getName());

		if (withProperties) {
			json.put("properties", serializeMap(getProfileProperties(profile), true));
		}

		return json;
	}

	private Map<String, String> getProfileProperties(GameProfile profile) {
		Map<String, String> properties = new HashMap<>();
		try {
			properties.put("textures", Base64.getEncoder().encodeToString(toTexturePayload(profile).toString().getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("utf-8 not supported", e);
		}
		return properties;
	}

	private JSONObject toTexturePayload(GameProfile profile) {
		JSONObject payload = new JSONObject();
		payload.put("timestamp", System.currentTimeMillis());
		payload.put("profileId", profile.getUuid());
		payload.put("profileName", profile.getName());
		payload.put("isPublic", true);

		JSONObject textureEntries = new JSONObject();
		Map<String, String> textureProperties = toTextureProperties(profile);
		if (profile.getSkin() != null)
			textureEntries.put("SKIN", toTextureEntry(profile.getSkin(), textureProperties));
		if (profile.getCape() != null)
			textureEntries.put("CAPE", toTextureEntry(profile.getCape(), null));
		if (profile.getElytra() != null)
			textureEntries.put("ELYTRA", toTextureEntry(profile.getElytra(), null));

		payload.put("textures", textureEntries);
		return payload;
	}

	private JSONObject toTextureEntry(String url, Map<String, String> properties) {
		JSONObject entry = new JSONObject();
		entry.put("url", url);
		if (properties != null)
			entry.put("metadata", properties);
		return entry;
	}

	private Map<String, String> toTextureProperties(GameProfile texture) {
		Map<String, String> properties = new HashMap<>();
		properties.put("model", texture.getTextureModel().getModelName());
		return properties;
	}

	@Override
	public JSONObject serializeAuthenticateResponse(AuthenticateResponse auth, boolean withProfiles) {
		JSONObject resp = new JSONObject();
		resp.put("accessToken", auth.getAccessToken());
		resp.put("clientToken", auth.getClientToken());

		if (auth.getSelectedProfile() != null) {
			resp.put("selectedProfile", serializeGameProfile(auth.getSelectedProfile(), false));
		}

		if (withProfiles && auth.getProfiles() != null) {
			JSONArray profilesResp = new JSONArray();
			for (GameProfile profile : auth.getProfiles()) {
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
