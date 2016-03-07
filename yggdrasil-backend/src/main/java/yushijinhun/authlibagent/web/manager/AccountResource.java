package yushijinhun.authlibagent.web.manager;

import java.util.Collection;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import yushijinhun.authlibagent.util.ResourceUtils;

@Path("/accounts")
@Produces(ResourceUtils.APPLICATION_JSON_UTF8)
public interface AccountResource {

	@GET
	Collection<String> getAccounts(@QueryParam("accessToken") String accessToken, @QueryParam("clientToken") String clientToken, @QueryParam("banned") Boolean banned, @QueryParam("twitchToken") String twitchToken);

	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	AccountInfo createAccount(AccountInfo account);

	@GET
	@Path("{id}")
	AccountInfo getAccountInfo(@PathParam("id") String id);

	@DELETE
	@Path("{id}")
	void deleteAccount(@PathParam("id") String id);

	@Consumes(MediaType.APPLICATION_JSON)
	@PUT
	@Path("{id}")
	AccountInfo updateOrCreateAccount(@PathParam("id") String id, AccountInfo account);

	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	@Path("{id}")
	AccountInfo updateAccount(@PathParam("id") String id, AccountInfo account);

}
