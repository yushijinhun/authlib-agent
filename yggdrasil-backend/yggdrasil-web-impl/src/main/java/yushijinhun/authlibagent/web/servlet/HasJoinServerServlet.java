package yushijinhun.authlibagent.web.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import yushijinhun.authlibagent.api.web.ForbiddenOperationException;
import yushijinhun.authlibagent.api.web.response.GameProfileResponse;
import yushijinhun.authlibagent.commons.AccessPolicy;

@WebServlet("/session/hasjoinserver")
public class HasJoinServerServlet extends YggdrasilGetServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected JSONObject process(HttpServletRequest req) throws Exception {
		String username = req.getParameter("username");
		String serverId = req.getParameter("serverId");

		// check access
		if (backend.getServerAccessPolicy(req.getRemoteAddr()) == AccessPolicy.DENY) {
			throw new ForbiddenOperationException("Blocked host");
		}

		GameProfileResponse profile = backend.hasJoinServer(username, serverId);

		if (profile == null) {
			return null;
		} else {
			return serializer.serializeGameProfile(profile, true);
		}
	}

}
