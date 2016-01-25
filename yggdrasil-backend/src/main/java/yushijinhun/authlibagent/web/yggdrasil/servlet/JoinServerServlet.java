package yushijinhun.authlibagent.web.yggdrasil.servlet;

import static yushijinhun.authlibagent.util.UUIDUtils.toUUID;
import java.util.UUID;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;

@WebServlet("/yggdrasil/joinserver")
public class JoinServerServlet extends YggdrasilPostServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected JSONObject process(JSONObject req, HttpServletRequest rawReq) throws Exception {
		String accessToken = req.getString("accessToken");
		UUID profile = toUUID(req.getString("selectedProfile"));
		String serverid = req.getString("serverId");
		backend.joinServer(accessToken, profile, serverid);
		return null;
	}

}
