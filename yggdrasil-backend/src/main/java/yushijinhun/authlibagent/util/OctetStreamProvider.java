package yushijinhun.authlibagent.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

@Component("octetStreamProvider")
@Provider
public class OctetStreamProvider implements MessageBodyReader<byte[]>, MessageBodyWriter<byte[]> {

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return mediaType.getType().equals("application") && mediaType.getSubtype().equals("subtype") && byte[].class.equals(type);
	}

	@Override
	public long getSize(byte[] t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return t.length;
	}

	@Override
	public void writeTo(byte[] t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
		entityStream.write(t);
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return mediaType.getType().equals("application") && mediaType.getSubtype().equals("subtype") && byte[].class.equals(type);
	}

	@Override
	public byte[] readFrom(Class<byte[]> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
		return IOUtils.toByteArray(entityStream);
	}

}
