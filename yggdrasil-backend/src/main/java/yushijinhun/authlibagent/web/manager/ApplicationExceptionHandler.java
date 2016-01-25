package yushijinhun.authlibagent.web.manager;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;

@Component("applicationExceptionHandler")
@Provider
public class ApplicationExceptionHandler implements ExceptionMapper<Throwable> {

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	@Autowired
	private ObjectMapper objectMapper;

	@Value("#{config['security.showStacktrace']}")
	private boolean showStacktrace;

	@Override
	public Response toResponse(Throwable e) {
		int status = Status.INTERNAL_SERVER_ERROR.getStatusCode();
		String errMsg = null;
		if (e instanceof WebApplicationException) {
			status = ((WebApplicationException) e).getResponse().getStatus();
			if (e.getCause() != null && status == Status.NOT_FOUND.getStatusCode()) {
				// error when handling arguments
				e = e.getCause();
				status = Status.BAD_REQUEST.getStatusCode();
			} else if (e.getMessage() != null) {
				errMsg = e.getMessage();
			}
		} else if (e instanceof JsonProcessingException) {
			status = Status.BAD_REQUEST.getStatusCode();
		} else {
			LOGGER.warn("Caught exception", e);
		}

		if (errMsg == null) {
			errMsg = e.getClass().getSimpleName();
			if (e.getMessage() != null) {
				errMsg += ": " + e.getMessage();
			}
		}

		ErrorResponse err = new ErrorResponse();
		err.setError(errMsg);
		err.setErrorCode(status);
		if (showStacktrace) {
			err.setStacktrace(Throwables.getStackTraceAsString(e));
		}
		try {
			return Response.status(status).type(MediaType.APPLICATION_JSON).entity(objectMapper.writeValueAsString(err)).build();
		} catch (JsonProcessingException e1) {
			LOGGER.error("Failed to serialize response", e1);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

}
