package yushijinhun.authlibagent.web;

import java.io.IOException;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import yushijinhun.authlibagent.api.web.WebBackend;

abstract public class YggdrasilServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Resource(name = "backend")
	protected WebBackend backend;

	@Resource(name = "error_messages")
	private Map<String, String> errorMessages;

	@Resource(name = "error_codes")
	private Map<String, Integer> errorCodes;

	@Value("#{config['security.showErrorCause']}")
	private boolean showErrorCause;

	@Override
	public void init() throws ServletException {
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, getServletContext());
	}

	protected void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		int respCode;
		JSONObject jsonResp;
		try {
			jsonResp = process(req, resp);
			if (jsonResp == null) {
				respCode = 204;
			} else {
				respCode = 200;
			}
		} catch (IOException | ServletException e) {
			throw e;
		} catch (Exception e) {
			log("exception during process request", e);

			respCode = getConfiguredErrorCode(e.getClass());
			if (respCode == -1) {
				respCode = 500;
			}

			String errorName = lookupErrorName(e);
			String message = e.getMessage();
			String cause = showErrorCause ? lookupErrorName(e.getCause()) : null;

			jsonResp = new JSONObject();
			jsonResp.put("error", errorName);
			if (message != null)
				jsonResp.put("errorMessage", message);
			if (cause != null)
				jsonResp.put("cause", cause);
		}

		resp.setStatus(respCode);
		if (jsonResp != null) {
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
			resp.getWriter().print(jsonResp);
		}
	}

	abstract protected JSONObject process(HttpServletRequest req, HttpServletResponse resp) throws Exception;

	private String lookupErrorName(Throwable e) {
		if (e == null) {
			return null;
		}
		String exName = getConfiguredErrorName(e.getClass());
		if (exName == null) {
			exName = e.getClass().getSimpleName();
		}
		return exName;
	}

	private String getConfiguredErrorName(Class<?> clazz) {
		String exName = null;
		for (Class<?> currentClass = clazz; currentClass != null; currentClass = currentClass.getSuperclass()) {
			exName = errorMessages.get(currentClass.getCanonicalName());
			if (exName != null) {
				break;
			}
		}
		return exName;
	}

	private int getConfiguredErrorCode(Class<?> clazz) {
		int exCode = -1;
		for (Class<?> currentClass = clazz; currentClass != null; currentClass = currentClass.getSuperclass()) {
			exCode = errorCodes.get(currentClass.getCanonicalName());
			if (exCode != -1) {
				break;
			}
		}
		return exCode;
	}

}
