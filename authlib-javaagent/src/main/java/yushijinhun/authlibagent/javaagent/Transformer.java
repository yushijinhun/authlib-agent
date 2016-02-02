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
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import yushijinhun.authlibagent.javaagent.TransformUnit.TransformCheckCallback;

public class Transformer implements ClassFileTransformer {

	protected static class TransformCheckCallbackImpl implements TransformCheckCallback {

		boolean isAllowed = false;

		@Override
		public void allowTransform() {
			isAllowed = true;
		}

	}

	protected final Logger logger = Logger.getLogger(getClass().getCanonicalName());

	protected Map<String, Collection<TransformUnit>> units = new ConcurrentHashMap<>();
	protected boolean debugMode;

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

	public synchronized void debugOn(){
		if (debugMode){
			return;
		}
		debugMode=true;
		try {
			logger.addHandler(new FileHandler("authlibagent.log"));
		} catch (SecurityException | IOException e) {
			logger.severe("failed to add file handler" + e);
			e.printStackTrace();
		}
	}

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		if (className != null) {
			try {
				Collection<TransformUnit> classunits = units.get(className);
				if (classunits != null) {
					logger.info("try to transform " + className);
					byte[] output = transformClass(classunits, classfileBuffer);
					if (output != null) {
						logger.info("transform " + className);
						if (debugMode) {
							debugSaveModifiedClass(output, className);
						}
						return output;
					}
				}
			} catch (Throwable e) {
				logger.severe("failed to transform " + className);
				e.printStackTrace();
			}
		}
		return null;
	}

	protected void debugSaveModifiedClass(byte[] classBuffer, String className) {
		className = className.replace('/', '.');
		try (OutputStream out = new FileOutputStream(className + "_modified.class")) {
			out.write(classBuffer);
		} catch (IOException e) {
			logger.warning("failed to save modified class " + className + "　: " + e);
			e.printStackTrace();
		}
	}

	protected byte[] transformClass(Collection<TransformUnit> classunits, byte[] classbuffer) {
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
				logger.info("transform unit " + unit);
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
