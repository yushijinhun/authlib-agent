package yushijinhun.authlibagent.web;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import yushijinhun.authlibagent.service.rmi.YggdrasilBackendLocal;
import yushijinhun.authlibagent.web.exception.YggdrasilException;

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

}
