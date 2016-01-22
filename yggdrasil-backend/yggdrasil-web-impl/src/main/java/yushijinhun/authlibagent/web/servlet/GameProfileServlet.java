package yushijinhun.authlibagent.web.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import yushijinhun.authlibagent.api.web.response.GameProfileResponse;
import java.util.UUID;
import static yushijinhun.authlibagent.commons.UUIDUtils.*;

@WebServlet("/profiles/minecraft/*")
public class GameProfileServlet extends YggdrasilGetServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected JSONObject process(HttpServletRequest req) throws Exception {
		String path = req.getPathInfo().substring(1); // delete the / at the beginning
		if (path == null) {
			return null;
		}

		UUID profileUUID = toUUID(path);
		GameProfileResponse profile = backend.lookupProfile(profileUUID);

		return serializer.serializeGameProfile(profile, true);
	}

}
