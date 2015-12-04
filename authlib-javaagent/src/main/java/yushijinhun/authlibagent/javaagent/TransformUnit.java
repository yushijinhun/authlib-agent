package yushijinhun.authlibagent.javaagent;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public interface TransformUnit {

	public static interface TransformCheckCallback {

		void allowTransform();

	}

	ClassVisitor check(TransformCheckCallback callback);

	ClassVisitor transform(ClassWriter writer);
	
}
