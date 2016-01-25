package yushijinhun.authlibagent.util;

import org.springframework.beans.factory.FactoryBean;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonFactory.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ObjectMapperBean implements FactoryBean<ObjectMapper> {

	@Override
	public ObjectMapper getObject() throws Exception {
		JsonFactory jsonFactory = new JsonFactory();

		// disable the thread local to prevent memory leak
		jsonFactory.disable(Feature.USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING);

		ObjectMapper objectMapper = new ObjectMapper(jsonFactory);
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

		return objectMapper;
	}

	@Override
	public Class<?> getObjectType() {
		return ObjectMapper.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
