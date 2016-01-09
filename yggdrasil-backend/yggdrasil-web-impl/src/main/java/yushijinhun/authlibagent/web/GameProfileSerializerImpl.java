package yushijinhun.authlibagent.web;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import yushijinhun.authlibagent.api.web.response.GameProfileResponse;
import yushijinhun.authlibagent.commons.PlayerTexture;
import static yushijinhun.authlibagent.commons.UUIDUtils.*;

@Component("game_profile_serializer")
public class GameProfileSerializerImpl implements GameProfileSerializer {

	@Resource(name = "signature_service")
	private SignatureService signatureService;

	@Override
	public JSONObject serialize(GameProfileResponse profile, boolean withProperties, boolean sign) {
		JSONObject json = new JSONObject();
		json.put("id", unsign(profile.getUUID()));
		json.put("name", profile.getName());

		if (withProperties) {
			json.put("properties", serializeMap(getProperties(profile), sign));
		}

		return json;
	}

	private Map<String, String> getProperties(GameProfileResponse profile) {
		Map<String, String> properties = new HashMap<>();
		try {
			properties.put("textures", Base64.getEncoder().encodeToString(toTexturePayload(profile).toString().getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("utf-8 not supported", e);
		}
		return properties;
	}

	private JSONArray serializeMap(Map<String, String> map, boolean sign) {
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
				entry.put("signature", Base64.getEncoder().encode(signature));
			}
			entries.put(entry);
		});
		return entries;
	}

	private JSONObject toTexturePayload(GameProfileResponse profile) {
		JSONObject payload = new JSONObject();
		payload.put("timestamp", System.currentTimeMillis());
		payload.put("profileId", unsign(profile.getUUID()));
		payload.put("profileName", profile.getName());
		payload.put("isPublic", true);

		JSONObject textureEntries = new JSONObject();
		PlayerTexture texture = profile.getTexture();
		Map<String, String> textureProperties = getTextureProperties(texture);
		if (texture.getSkin() != null)
			textureEntries.put("SKIN", toTextureEntry(texture.getSkin(), textureProperties));
		if (texture.getCape() != null)
			textureEntries.put("CAPE", toTextureEntry(texture.getCape(), textureProperties));

		payload.put("textures", textureEntries);
		return payload;
	}

	private Map<String, String> getTextureProperties(PlayerTexture texture) {
		Map<String, String> properties = new HashMap<>();
		properties.put("model", texture.getModel().getModelName());
		return properties;
	}

	private JSONObject toTextureEntry(String url, Map<String, String> properties) {
		JSONObject entry = new JSONObject();
		entry.put("url", url);
		entry.put("metadata", serializeMap(properties, false));
		return entry;
	}

}
