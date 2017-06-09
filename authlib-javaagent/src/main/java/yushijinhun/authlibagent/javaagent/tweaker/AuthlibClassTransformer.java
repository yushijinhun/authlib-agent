package yushijinhun.authlibagent.javaagent.tweaker;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import yushijinhun.authlibagent.javaagent.AuthlibAgent;

/**
 * @author ustc_zzzz
 */
public class AuthlibClassTransformer implements IClassTransformer {

    private final LaunchClassLoader classLoader = Launch.classLoader;
	private final ClassFileTransformer transformer = AuthlibAgent.createTransformer();

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (name == null || bytes == null) return bytes;
        try {
            byte[] result = this.transformer.transform(this.classLoader, name, null, null, bytes);
            return result == null ? bytes : result;
        } catch (IllegalClassFormatException e) {
            throw new RuntimeException(e);
        }
    }

}
