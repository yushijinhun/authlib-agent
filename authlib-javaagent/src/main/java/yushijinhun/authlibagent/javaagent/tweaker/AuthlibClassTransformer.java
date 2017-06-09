package yushijinhun.authlibagent.javaagent.tweaker;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import yushijinhun.authlibagent.javaagent.AuthlibAgent;
import yushijinhun.authlibagent.javaagent.AuthlibTransformer;

import java.io.*;
import java.lang.instrument.IllegalClassFormatException;
import java.nio.file.Files;
import java.util.Properties;

/**
 * @author ustc_zzzz
 */
public class AuthlibClassTransformer implements IClassTransformer {
    private final LaunchClassLoader classLoader = Launch.classLoader;
    private final AuthlibTransformer transformer = AuthlibAgent.getTransformer("", this.readProperties());

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        try {
            byte[] result = this.transformer.transform(this.classLoader, name, null, null, bytes);
            return result == null ? bytes : result;
        } catch (IllegalClassFormatException e) {
            throw new RuntimeException(e);
        }
    }

    private Properties readProperties() {
        Properties properties = new Properties();
        File file = new File(Launch.minecraftHome, "authlib-config.properties");
        if (!file.exists()) {
            try {
                Files.copy(AuthlibAgent.class.getResourceAsStream("/transform.properties"), file.toPath());
            } catch (IOException e) {
                throw new RuntimeException("failed to extract properties to " + file.getAbsolutePath(), e);
            }
        }
        try (Reader reader = new InputStreamReader(new FileInputStream(file), "UTF-8")) {
            properties.load(reader);
        } catch (IOException e) {
            AuthlibAgent.getLogger().warning("failed to read properties: " + e);
            e.printStackTrace();
        }
        return properties;
    }
}
