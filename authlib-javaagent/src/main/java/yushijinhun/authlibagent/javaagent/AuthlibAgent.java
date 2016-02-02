package yushijinhun.authlibagent.javaagent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.instrument.Instrumentation;
import java.util.Properties;
import java.util.logging.Logger;

public class AuthlibAgent {

	private static final Logger LOGGER = Logger.getLogger(AuthlibAgent.class.getCanonicalName());
	private static final int MAX_KEY_LENGTH = Short.MAX_VALUE;

	public static void premain(String arg, Instrumentation instrumentation) {
		try {
			init(arg, instrumentation);
			LOGGER.info("initialized transformer");
		} catch (Throwable e) {
			LOGGER.info("failed to initialize transformer: " + e);
			e.printStackTrace();
		}
	}

	private static void init(String arg, Instrumentation instrumentation) {
		Properties properties = readProperties();
		boolean debugMode = Boolean.parseBoolean(properties.getProperty("debug"));
		byte[] yggdrasilPublickey = readPublicKey();
		String apiYggdrasilAuthenticate = properties.getProperty("transform.yggdrasil.authenticate");
		String apiYggdrasilRefresh = properties.getProperty("transform.yggdrasil.refresh");
		String apiYggdrasilValidate = properties.getProperty("transform.yggdrasil.validate");
		String apiYgggdrasilInvalidate = properties.getProperty("transform.yggdrasil.invalidate");
		String apiYggdarsilSignout = properties.getProperty("transform.yggdrasil.signout");
		String apiFillGameProfile = properties.getProperty("transform.session.fillgameprofile");
		String apiJoinServer = properties.getProperty("transform.session.joinserver");
		String apiHasJoinServer = properties.getProperty("transform.session.hasjoinserver");
		String apiProfilesLookup = properties.getProperty("transform.api.profiles");
		String[] skinWhitelist = null;
		String skinWhitelistRaw = properties.getProperty("transform.skin.whitelistdomains");
		if (skinWhitelistRaw != null) {
			skinWhitelist = skinWhitelistRaw.split("\\|");
		}

		boolean exhaustive = true;
		if ("+exhaustive".equals(arg)) {
			exhaustive = true;
		} else if ("-exhaustive".equals(arg)) {
			exhaustive = false;
		} else if (arg != null && !arg.isEmpty()) {
			LOGGER.warning("Unknown option: " + arg);
		}

		AuthlibTransformer transformer;
		if (exhaustive) {
			transformer = new ExhaustiveTransformer(apiYggdrasilAuthenticate, apiYggdrasilRefresh, apiYggdrasilValidate, apiYgggdrasilInvalidate, apiYggdarsilSignout, apiFillGameProfile, apiJoinServer, apiHasJoinServer, apiProfilesLookup, skinWhitelist, yggdrasilPublickey);
		} else {
			transformer = new AuthlibTransformer(apiYggdrasilAuthenticate, apiYggdrasilRefresh, apiYggdrasilValidate, apiYgggdrasilInvalidate, apiYggdarsilSignout, apiFillGameProfile, apiJoinServer, apiHasJoinServer, apiProfilesLookup, skinWhitelist, yggdrasilPublickey);
		}

		if (debugMode) {
			transformer.debugOn();
		}

		instrumentation.addTransformer(transformer);
	}

	private static Properties readProperties() {
		Properties properties = new Properties();
		try (Reader reader = new InputStreamReader(AuthlibAgent.class.getResourceAsStream("/transform.properties"), "UTF-8")) {
			properties.load(reader);
		} catch (IOException e) {
			LOGGER.warning("failed to read properties: " + e);
			e.printStackTrace();
		}
		return properties;
	}

	private static byte[] readPublicKey() {
		byte[] key;
		try (InputStream in = AuthlibAgent.class.getResourceAsStream("/new_yggdrasil_session_pubkey.der")) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[] buffer = new byte[8192];
			int read;
			while ((read = in.read(buffer)) != -1) {
				bout.write(buffer, 0, read);
			}
			key = bout.toByteArray();
		} catch (IOException e) {
			LOGGER.warning("failed to read public key: " + e);
			e.printStackTrace();
			return null;
		}

		if (key.length > MAX_KEY_LENGTH) {
			LOGGER.info("public key too long: " + key.length + ", max: " + MAX_KEY_LENGTH);
			return null;
		}

		return key;
	}

}
