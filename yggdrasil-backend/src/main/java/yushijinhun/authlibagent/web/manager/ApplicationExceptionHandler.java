package yushijinhun.authlibagent.web.manager;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.springframework.stereotype.Component;

@Component("applicationExceptionHandler")
@Provider
public class ApplicationExceptionHandler  implements ExceptionMapper<WebApplicationException>{

	@Override
	public Response toResponse(WebApplicationException exception) {
		return Response.status(exception.getResponse().getStatus()).entity(exception.getResponse().getEntity() == null ? exception.getMessage() : exception.getResponse().getEntity()).type(MediaType.TEXT_PLAIN).build();
	}

}
