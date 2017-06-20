package yushijinhun.authlibagent.web.yggdrasil.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import yushijinhun.authlibagent.model.AccessPolicy;
import yushijinhun.authlibagent.model.GameProfile;
import yushijinhun.authlibagent.service.ForbiddenOperationException;
import yushijinhun.authlibagent.service.MojangYggdrasilService;

@WebServlet("/yggdrasil/hasjoinserver")
public class HasJoinServerServlet extends YggdrasilGetServlet {

	private static final long serialVersionUID = 1L;

	@Autowired
	private MojangYggdrasilService mojangService;

	@Override
	protected JSONObject process(HttpServletRequest req) throws Exception {
		String username = req.getParameter("username");
		String serverId = req.getParameter("serverId");

		if (username == null || serverId == null) throw new IllegalArgumentException();

		// check access
		if (backend.getServerAccessPolicy(getIpAddress(req)) == AccessPolicy.DENY) {
			throw new ForbiddenOperationException("Blocked host");
		}

		GameProfile profile = backend.hasJoinServer(username, serverId);

		if (profile == null) {
			return mojangService.hasJoinServer(username, serverId).orElse(null);
		} else {
			return serializer.serializeGameProfile(profile, true);
		}
	}

	private String getIpAddress(HttpServletRequest req) {
		String forwardIps = req.getHeader("X-Forwarded-For");
		if (forwardIps != null && !forwardIps.isEmpty()) {
			int idxComma = forwardIps.indexOf(',');
			if (idxComma == -1) {
				return forwardIps;
			} else {
				return forwardIps.substring(0, idxComma);
			}
		}
		return req.getRemoteAddr();
	}

}
