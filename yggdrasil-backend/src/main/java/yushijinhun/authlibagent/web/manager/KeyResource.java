package yushijinhun.authlibagent.web.manager;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/privatekey")
@Produces(MediaType.APPLICATION_OCTET_STREAM)
@Consumes(MediaType.APPLICATION_OCTET_STREAM)
public interface KeyResource {

	Response getEncodedKey();

	void setEncodedKey(@Context HttpServletRequest req) throws IOException;

}
