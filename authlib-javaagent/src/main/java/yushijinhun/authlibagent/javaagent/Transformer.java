package yushijinhun.authlibagent.javaagent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import yushijinhun.authlibagent.javaagent.TransformUnit.TransformCheckCallback;

public class Transformer implements ClassFileTransformer {

	private static final Logger LOGGER = Logger.getLogger("authlibagent.transformer");

	private static class TransformCheckCallbackImpl implements TransformCheckCallback {

		boolean isAllowed = false;

		@Override
		public void allowTransform() {
			isAllowed = true;
		}

	}

	private Map<String, Collection<TransformUnit>> units = new ConcurrentHashMap<>();
	private boolean debugMode;

	protected void addTransformUnit(String className, TransformUnit unit) {
		Objects.requireNonNull(className);
		Objects.requireNonNull(unit);
		className = className.replace('.', '/');
		synchronized (units) {
			Collection<TransformUnit> classUnits = units.get(className);
			if (classUnits == null) {
				classUnits = new CopyOnWriteArrayList<>();
				units.put(className, classUnits);
			}
			classUnits.add(unit);
		}
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		if (className != null) {
			try {
				Collection<TransformUnit> classunits = units.get(className);
				if (classunits != null) {
					LOGGER.info("try to transform " + className);
					byte[] output = transformClass(classunits, classfileBuffer);
					if (output != null) {
						LOGGER.info("transform " + className);
						if (debugMode) {
							debugSaveModifiedClass(output, className);
						}
						return output;
					}
				}
			} catch (Throwable e) {
				LOGGER.info("failed to transform " + className);
				e.printStackTrace();
			}
		}
		return null;
	}

	private void debugSaveModifiedClass(byte[] classBuffer, String className) {
		className = className.replace('/', '.');
		try (OutputStream out = new FileOutputStream(className + "_modified.class")) {
			out.write(classBuffer);
		} catch (IOException e) {
			LOGGER.warning("failed to save modified class " + className + "　: " + e);
			e.printStackTrace();
		}
	}

	private byte[] transformClass(Collection<TransformUnit> classunits, byte[] classbuffer) {
		if (units == null) {
			return null;
		}

		boolean changed = false;
		byte[] currentClass = classbuffer;
		ClassReader classreader = new ClassReader(currentClass);
		for (TransformUnit unit : classunits) {
			TransformCheckCallbackImpl callback = new TransformCheckCallbackImpl();
			ClassVisitor checker = unit.check(callback);
			if (checker != null) {
				classreader.accept(checker, ClassReader.SKIP_DEBUG);
			}
			if (callback.isAllowed) {
				LOGGER.info("transform unit " + unit);
				ClassWriter classwriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
				ClassVisitor modifier = unit.transform(classwriter);
				classreader.accept(modifier, 0);
				currentClass = classwriter.toByteArray();
				classreader = new ClassReader(currentClass);
				changed = true;
			}
		}
		if (changed) {
			return currentClass;
		}
		return null;
	}

}
