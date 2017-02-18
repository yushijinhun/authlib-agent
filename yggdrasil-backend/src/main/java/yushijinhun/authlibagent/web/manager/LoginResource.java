package yushijinhun.authlibagent.web.manager;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import yushijinhun.authlibagent.util.ResourceUtils;

@Path("/login")
@Produces(ResourceUtils.APPLICATION_JSON_UTF8)
public interface LoginResource {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	AccountInfo login(LoginParam param);

}
