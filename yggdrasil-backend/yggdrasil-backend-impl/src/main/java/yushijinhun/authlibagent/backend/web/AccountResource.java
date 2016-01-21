package yushijinhun.authlibagent.backend.web;

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

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AccountResource {

	@GET
	String[] getAccounts(@QueryParam("accessToken") String accessToken, @QueryParam("clientToken") String clientToken, @QueryParam("banned") Boolean banned, @QueryParam("twitchToken") String twitchToken);

	@POST
	AccountInfo createAccount(AccountInfo account);

	@GET
	@Path("{id}")
	AccountInfo getAccountInfo(@PathParam("id") String id);

	@DELETE
	@Path("{id}")
	void deleteAccount(@PathParam("id") String id);

	@PUT
	@Path("{id}")
	AccountInfo updateAccount(@PathParam("id") String id, AccountInfo account);

}
