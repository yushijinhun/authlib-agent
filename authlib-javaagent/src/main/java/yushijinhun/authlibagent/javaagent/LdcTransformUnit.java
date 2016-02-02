package yushijinhun.authlibagent.javaagent;

import java.util.Objects;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LdcTransformUnit extends NamedTransformUnit {

	abstract class LdcClassVisitor extends ClassVisitor {

		LdcClassVisitor() {
			super(Opcodes.ASM4);
		}

		LdcClassVisitor(ClassVisitor cv) {
			super(Opcodes.ASM4, cv);
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
			if (method.equals(name)) {
				return new MethodVisitor(Opcodes.ASM4, mv) {

					@Override
					public void visitLdcInsn(Object cst) {
						if (Objects.equals(ldc, cst)) {
							checked(mv);
						} else {
							super.visitLdcInsn(cst);
						}
					}
				};
			} else {
				return mv;
			}
		}

		abstract void checked(MethodVisitor mv);

	}

	Object method;
	Object ldc;
	Object replacement;

	LdcTransformUnit(String name, Object method, Object ldc, Object replacement) {
		super(name);
		this.method = method;
		this.ldc = ldc;
		this.replacement = replacement;
	}

	@Override
	public ClassVisitor check(final TransformCheckCallback callback) {
		if (replacement != null) {
			return new LdcClassVisitor() {

				@Override
				void checked(MethodVisitor mv) {
					callback.allowTransform();
				}
			};
		}
		return null;
	}

	@Override
	public ClassVisitor transform(ClassWriter writer) {
		return new LdcClassVisitor(writer) {

			@Override
			void checked(MethodVisitor mv) {
				mv.visitLdcInsn(replacement);
			}
		};
	}

}