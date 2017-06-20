package yushijinhun.authlibagent.web.yggdrasil.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import yushijinhun.authlibagent.model.GameProfile;
import yushijinhun.authlibagent.service.MojangYggdrasilService;

@WebServlet("/yggdrasil/username2profile/*")
public class Username2ProfileServlet extends YggdrasilGetServlet {

	private static final long serialVersionUID = 1L;

	@Autowired
	private MojangYggdrasilService mojangService;

	@Override
	protected JSONObject process(HttpServletRequest req) throws Exception {
		String path = req.getPathInfo();
		if (path == null) {
			return null;
		}

		// substring() - delete the / at the beginning
		String username = path.substring(1);
		GameProfile profile = backend.lookupProfile(username);
		if (profile != null) {
			return serializer.serializeGameProfile(profile, true);
		} else {
			return mojangService.profileSearch(username).orElse(null);
		}
	}

}
