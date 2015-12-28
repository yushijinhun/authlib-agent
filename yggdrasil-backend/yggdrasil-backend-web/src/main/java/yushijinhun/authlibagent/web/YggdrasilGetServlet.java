package yushijinhun.authlibagent.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import yushijinhun.authlibagent.web.exception.YggdrasilException;

abstract public class YggdrasilGetServlet extends YggdrasilServlet {

	private static final long serialVersionUID = 1L;

	abstract protected JSONObject process(HttpServletRequest req) throws YggdrasilException;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleRequest(req, resp);
	}

	@Override
	protected JSONObject process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, YggdrasilException {
		return process(req);
	}

}
