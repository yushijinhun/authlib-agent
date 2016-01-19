package yushijinhun.authlibagent.web.servlet;

import static yushijinhun.authlibagent.commons.UUIDUtils.toUUID;
import java.util.UUID;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;

@WebServlet("/yggdrasil/invalidate")
public class InvalidateServlet extends YggdrasilPostServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected JSONObject process(JSONObject req, HttpServletRequest rawReq) throws Exception {
		UUID clientToken = toUUID(req.getString("clientToken"));
		UUID accessToken = toUUID(req.getString("accessToken"));
		backend.invalidate(accessToken, clientToken);
		return null;
	}

}
