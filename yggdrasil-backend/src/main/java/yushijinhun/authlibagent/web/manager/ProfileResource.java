package yushijinhun.authlibagent.web.manager;

import java.util.Collection;
import java.util.UUID;
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
import yushijinhun.authlibagent.model.TextureModel;
import yushijinhun.authlibagent.util.ResourceUtils;

@Path("/profiles")
@Produces(ResourceUtils.APPLICATION_JSON_UTF8)
public interface ProfileResource {

	@GET
	Collection<String> getProfiles(@QueryParam("name") String name, @QueryParam("owner") String owner, @QueryParam("banned") Boolean banned, @QueryParam("skin") String skin, @QueryParam("cape") String cape, @QueryParam("elytra") String elytra, @QueryParam("model") TextureModel model, @QueryParam("serverId") String serverId);

	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	ProfileInfo createProfile(ProfileInfo profile);

	@GET
	@Path("{uuid}")
	ProfileInfo getProfileInfo(@PathParam("uuid") UUID uuid);

	@DELETE
	@Path("{uuid}")
	void deleteProfile(@PathParam("uuid") UUID uuid);

	@Consumes(MediaType.APPLICATION_JSON)
	@PUT
	@Path("{uuid}")
	ProfileInfo updateOrCreateProfile(@PathParam("uuid") UUID uuid, ProfileInfo profile);

	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	@Path("{uuid}")
	ProfileInfo updateProfile(@PathParam("uuid") UUID uuid, ProfileInfo profile);

}
