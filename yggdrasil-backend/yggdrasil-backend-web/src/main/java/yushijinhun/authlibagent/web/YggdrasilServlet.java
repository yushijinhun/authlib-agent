package yushijinhun.authlibagent.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import yushijinhun.authlibagent.api.AlreadyDeletedException;
import yushijinhun.authlibagent.api.PlayerTexture;
import yushijinhun.authlibagent.api.TextureModel;
import yushijinhun.authlibagent.service.rmi.GameProfileLocal;
import yushijinhun.authlibagent.service.rmi.YggdrasilBackendLocal;
import yushijinhun.authlibagent.web.exception.YggdrasilException;
import static yushijinhun.authlibagent.util.UUIDUtils.*;

abstract public class YggdrasilServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected YggdrasilBackendLocal backend;

	@Override
	public void init() throws ServletException {
		super.init();

		ServletContext servletContext = getServletContext();
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		backend = ctx.getBean("backend_access", YggdrasilBackendLocal.class);
	}

	protected void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject jsonResp;
		int rescode;

		try {
			jsonResp = process(req, resp);
			rescode = (jsonResp == null ? 204 : 200);
		} catch (YggdrasilException e) {
			log("process exception", e);
			jsonResp = processException(e);
			rescode = e.getResponseCode();
		}

		resp.setStatus(rescode);
		if (jsonResp != null) {
			resp.setCharacterEncoding("UTF-8");
			resp.setContentType("application/json");
			resp.getWriter().write(jsonResp.toString());
		}
	}

	abstract protected JSONObject process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, YggdrasilException;

	private JSONObject processException(YggdrasilException e) {
		JSONObject resp = new JSONObject();

		if (e.getErrorName() == null) {
			throw new IllegalArgumentException("error name cannot be null");
		}
		resp.put("error", e.getErrorName());

		if (e.getErrorMessage() != null) {
			resp.put("errorMessage", e.getErrorMessage());
		}

		if (e.getErrorCause() != null) {
			resp.put("cause", e.getErrorCause());
		}

		return resp;
	}

	protected JSONObject jsonProfile(UUID uuid) {
		GameProfileLocal profile = backend.getAccountManager().lookupGameProfile(uuid);
		if (profile == null) {
			return null;
		}

		try {
			JSONObject json = new JSONObject();
			json.put("id", unsign(uuid));
			json.put("name", profile.getName());
			return json;
		} catch (AlreadyDeletedException e) {
			log("profile deleted after found", e);
			return null;
		}
	}

	protected JSONObject jsonPropertiesProfile(UUID uuid, boolean sign) {
		GameProfileLocal profile = backend.getAccountManager().lookupGameProfile(uuid);
		if (profile == null) {
			return null;
		}

		try {
			JSONObject json = new JSONObject();
			json.put("id", unsign(uuid));
			json.put("name", profile.getName());

			Map<String, String> properties = new HashMap<>();

			try {
				properties.put("textures", Base64.getEncoder().encodeToString(jsonTexturePayload(profile.getTexture()).toString().getBytes("UTF-8")));
			} catch (UnsupportedEncodingException e) {
				log("utf8 not supported", e);
			}

			json.put("properties", jsonProperties(properties, sign));
			return json;
		} catch (AlreadyDeletedException e) {
			log("profile deleted after found", e);
			return null;
		}
	}

	protected JSONArray jsonProperties(Map<String, String> props, boolean sign) {
		JSONArray array = new JSONArray();
		props.forEach((k, v) -> {
			JSONObject entry = new JSONObject();
			entry.put("name", k);
			entry.put("value", v);

			if (sign) {
				try {
					entry.put("signature", Base64.getEncoder().encode(backend.getSignatureService().sign(v.getBytes("UTF-8"))));
				} catch (UnsupportedEncodingException | GeneralSecurityException e) {
					log("unable to sign " + k, e);
				}
			}
		});
		return array;
	}

	protected JSONObject jsonTexturePayload(PlayerTexture texture) {
		JSONObject payload = new JSONObject();
		JSONObject textures = new JSONObject();
		TextureModel model = texture.getModel();
		if (texture.getSkin() != null)
			textures.put("SKIN", jsonTexture(texture.getSkin(), model));
		if (texture.getCape() != null)
			textures.put("CAPE", jsonTexture(texture.getSkin(), model));
		payload.put("textures", textures);
		return payload;
	}

	private JSONObject jsonTexture(String url, TextureModel model) {
		JSONObject texture = new JSONObject();
		texture.put("url", url);
		JSONObject metadata = new JSONObject();
		metadata.put("model", model.getModelName());
		texture.put("metadata", metadata);
		return texture;
	}

}
