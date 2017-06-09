package yushijinhun.authlibagent.javaagent.tweaker;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author ustc_zzzz
 */
public class AuthlibTweaker implements ITweaker {

	private static final Logger LOGGER = Logger.getLogger(AuthlibTweaker.class.getCanonicalName());

	@Override
	public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
		LOGGER.info("loading AuthlibTweaker");
	}

	@Override
	public void injectIntoClassLoader(LaunchClassLoader launchClassLoader) {
		LOGGER.info("injecting AuthlibClassTransformer into LaunchClassLoader");
		launchClassLoader.registerTransformer(AuthlibClassTransformer.class.getName());
	}

	@Override
	public String getLaunchTarget() {
		return "net.minecraft.client.main.Main";
	}

	@Override
	public String[] getLaunchArguments() {
		return new String[0];
	}
}
