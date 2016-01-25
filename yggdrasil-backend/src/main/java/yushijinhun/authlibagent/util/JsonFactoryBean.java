package yushijinhun.authlibagent.util;

import org.springframework.beans.factory.FactoryBean;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonFactory.Feature;

public class JsonFactoryBean implements FactoryBean<JsonFactory> {

	@Override
	public JsonFactory getObject() throws Exception {
		JsonFactory jsonFactory = new JsonFactory();

		// disable the thread local to prevent memory leak
		jsonFactory.disable(Feature.USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING);

		return jsonFactory;
	}

	@Override
	public Class<?> getObjectType() {
		return JsonFactory.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
