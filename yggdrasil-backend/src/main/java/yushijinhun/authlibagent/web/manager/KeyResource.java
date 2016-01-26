package yushijinhun.authlibagent.web.manager;

import java.io.IOException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/privatekey.der")
public interface KeyResource {

	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	byte[] getEncodedKey();

	@POST
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	void setEncodedKey(byte[] key) throws IOException;

}
