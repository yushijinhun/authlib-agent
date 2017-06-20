package yushijinhun.authlibagent.service;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import yushijinhun.authlibagent.dao.ProfileCacheRepository;
import yushijinhun.authlibagent.util.UUIDUtils;

@Component
public class MojangYggdrasilService {

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	private RestTemplate rest = new RestTemplate();

	@Autowired
	private SignatureService signatureService;

	@Autowired
	private ProfileCacheRepository cacheRepo;

	@Value("#{config['feature.optionalSignature']}")
	private boolean optionalSignature;

	@Value("#{config['feature.allowMojangUsers']}")
	private boolean allowMojangUsers;

	public Optional<JSONObject> hasJoinServer(String playername, String serverid) {
		if (!allowMojangUsers) return Optional.empty();
		try {
			return Optional.ofNullable(rest.getForObject("https://sessionserver.mojang.com/session/minecraft/hasJoined?username={username}&serverId={serverId}", String.class, playername, serverid))
					.map(JSONObject::new)
					.map(this::resignProfile);
		} catch (HttpClientErrorException e) {
			LOGGER.warn("Couldn't request mojang server", e);
			return Optional.empty();
		}
	}

	public Optional<JSONObject> getGameProfile(UUID uuid) {
		if (!allowMojangUsers) return Optional.empty();
		try {
			Optional<JSONObject> result = Optional.ofNullable(rest.getForObject("https://sessionserver.mojang.com/session/minecraft/profile/{uuid}", String.class, UUIDUtils.unsign(uuid)))
					.map(JSONObject::new)
					.map(this::resignProfile);
			result.ifPresent(profile -> cacheRepo.put(uuid, profile));
			return result;
		} catch (HttpClientErrorException e) {
			LOGGER.warn("Couldn't request mojang server", e);
			return cacheRepo.get(uuid);
		}
	}

	public Optional<JSONObject> profileSearch(String name) {
		if (!allowMojangUsers) return Optional.empty();
		try {
			return Optional.ofNullable(rest.getForObject("https://api.mojang.com/users/profiles/minecraft/{username}", String.class, name))
					.map(JSONObject::new)
					.map(this::resignProfile);
		} catch (HttpClientErrorException e) {
			LOGGER.warn("Couldn't request mojang server", e);
			return Optional.empty();
		}
	}

	private JSONObject resignProfile(JSONObject json) {
		JSONArray properties = json.optJSONArray("properties");
		if (properties != null) {
			for (Object element : properties) {
				JSONObject prop = (JSONObject) element;
				byte[] signature;
				try {
					signature = signatureService.sign(prop.getString("value").getBytes("UTF-8"));
					prop.put("signature", Base64.getEncoder().encodeToString(signature));
				} catch (UnsupportedEncodingException | GeneralSecurityException e) {
					if (optionalSignature) {
						// ignore
						LOGGER.debug("unable to sign, skipping", e);
						prop.remove("signature");
					} else {
						throw new IllegalStateException("unable to sign", e);
					}
				}
			}
		}
		return json;
	}

}
