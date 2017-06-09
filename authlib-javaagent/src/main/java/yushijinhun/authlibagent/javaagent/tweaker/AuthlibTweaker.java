package yushijinhun.authlibagent.javaagent.tweaker;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import yushijinhun.authlibagent.javaagent.AuthlibAgent;

import java.io.File;
import java.util.List;

/**
 * @author ustc_zzzz
 */
public class AuthlibTweaker implements ITweaker {
    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        AuthlibAgent.getLogger().info("loading AuthlibTweaker");
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader launchClassLoader) {
        String transformerClassName = "yushijinhun.authlibagent.javaagent.tweaker.AuthlibClassTransformer";
        launchClassLoader.registerTransformer(transformerClassName);
        AuthlibAgent.getLogger().info("injecting AuthlibClassTransformer into LaunchClassLoader");
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
