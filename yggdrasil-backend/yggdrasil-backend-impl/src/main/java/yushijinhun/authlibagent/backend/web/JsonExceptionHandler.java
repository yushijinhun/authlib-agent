package yushijinhun.authlibagent.backend.web;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;

@Component("jsonExceptionHandler")
@Provider
public class JsonExceptionHandler implements ExceptionMapper<JsonProcessingException> {

	@Override
	public Response toResponse(JsonProcessingException exception) {
		return Response.status(Status.BAD_REQUEST).entity(exception.getMessage()).type(MediaType.TEXT_PLAIN).build();
	}

}
